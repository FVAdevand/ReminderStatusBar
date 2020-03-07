package ua.fvadevand.reminderstatusbar.ui

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderItem
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.data.models.SnackbarData
import ua.fvadevand.reminderstatusbar.managers.AlarmManager
import ua.fvadevand.reminderstatusbar.managers.NotificationManager
import ua.fvadevand.reminderstatusbar.managers.PreferencesManager
import ua.fvadevand.reminderstatusbar.utils.SingleLiveEvent
import ua.fvadevand.reminderstatusbar.utils.SortUtils
import java.util.Collections

class RemindersViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationContext: Context get() = getApplication()
    private val repository = ReminderApp.getRepository()
    private val prefManager = PreferencesManager(applicationContext)
    private val alarmManager by lazy { AlarmManager(applicationContext) }
    private val notificationManager by lazy { NotificationManager(applicationContext) }
    private val _remindersSortedLive: MediatorLiveData<List<ReminderItem>> = MediatorLiveData()
    private val reminderSortOrderAscLive: MutableLiveData<Boolean> = MutableLiveData()
    private val reminderSortFieldLive: MutableLiveData<String> = MutableLiveData()
    private val remindersFromDb by lazy { repository.getAllLiveReminders() }
    private var sortJob: Job? = null
    val showSnackbar = SingleLiveEvent<SnackbarData?>()

    var nightMode
        get() = prefManager.nightMode
        set(value) {
            prefManager.nightMode = value
            AppCompatDelegate.setDefaultNightMode(value)
        }
    val remindersSortedLive: LiveData<List<ReminderItem>> by lazy {
        reminderSortFieldLive.postValue(prefManager.reminderSortField)
        reminderSortOrderAscLive.postValue(prefManager.reminderSortOrderAsc)
        _remindersSortedLive.addSource(remindersFromDb) {
            if (it.isEmpty()) {
                _remindersSortedLive.postValue(it)
            } else {
                sortAndPostReminders(it)
            }
        }
        _remindersSortedLive.addSource(reminderSortFieldLive) {
            if (!remindersFromDb.value.isNullOrEmpty()) {
                val reminders = ArrayList<ReminderItem>(remindersFromDb.value ?: listOf())
                sortAndPostReminders(reminders)
            }
        }
        _remindersSortedLive.addSource(reminderSortOrderAscLive) {
            if (!remindersFromDb.value.isNullOrEmpty()) {
                val reminders = ArrayList<ReminderItem>(remindersFromDb.value ?: listOf())
                sortAndPostReminders(reminders)
            }
        }
        _remindersSortedLive
    }

    fun getReminder(id: Long, onSuccess: (Reminder?) -> Unit) =
        viewModelScope.launch {
            onSuccess(repository.getReminderById(id))
        }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminder.id = repository.addReminder(reminder)
            if (reminder.timestamp > System.currentTimeMillis()) {
                notificationManager.cancelNotification(reminder.id.hashCode())
                alarmManager.setAlarm(reminder)
            } else {
                notificationManager.showNotification(reminder)
                if (reminder.periodType > PeriodType.ONE_TIME) {
                    reminder.timestamp =
                        PeriodType.getNextAlarmTimeByType(reminder.periodType, reminder.timestamp)
                    alarmManager.setAlarm(reminder)
                    repository.editReminder(reminder)
                }
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            notificationManager.cancelNotification(id.hashCode())
            alarmManager.cancelAlarm(id)
            repository.deleteReminderById(id)
        }
    }

    fun notifyReminder(id: Long) {
        viewModelScope.launch {
            repository.getReminderById(id)?.let {
                alarmManager.cancelAlarm(id)
                it.status = ReminderStatus.NOTIFYING
                it.timestamp = System.currentTimeMillis()
                repository.editReminder(it)
                notificationManager.showNotification(it)
            }
        }
    }

    fun setReminderStatusDone(id: Long) {
        viewModelScope.launch {
            notificationManager.cancelNotification(id.hashCode())
            alarmManager.cancelAlarm(id)
            repository.getReminderById(id)?.let {
                it.status = ReminderStatus.DONE
                it.timestamp = System.currentTimeMillis()
                repository.editReminder(it)
            }
        }
    }

    fun setSortField(@Reminder.SortFields sortField: String) {
        if (sortField != reminderSortFieldLive.value) {
            prefManager.reminderSortField = sortField
            reminderSortFieldLive.postValue(sortField)
        } else {
            val sortOrderAsc = reminderSortOrderAscLive.value
                ?: PreferencesManager.DEFAULT_REMINDER_SORT_ORDER_ASC
            prefManager.reminderSortOrderAsc = !sortOrderAsc
            reminderSortOrderAscLive.postValue(!sortOrderAsc)
        }
    }

    private fun sortAndPostReminders(reminders: List<ReminderItem>) {
        sortJob?.cancel()
        sortJob = viewModelScope.launch(Dispatchers.IO) {
            val sortField = reminderSortFieldLive.value ?: Reminder.COLUMN_TIMESTAMP
            val sortOrderAsc = reminderSortOrderAscLive.value
                ?: PreferencesManager.DEFAULT_REMINDER_SORT_ORDER_ASC
            val comparator = when (sortField) {
                Reminder.COLUMN_TITLE -> SortUtils.ReminderTitleComparator(sortOrderAsc)
                Reminder.COLUMN_STATUS -> SortUtils.ReminderStatusComparator(sortOrderAsc)
                Reminder.COLUMN_TIMESTAMP -> SortUtils.ReminderTimeComparator(sortOrderAsc)
                else -> throw IllegalArgumentException(
                    "SortField must be in @Reminder.SortFields, current value = $sortField "
                )
            }
            Collections.sort(reminders, comparator)
            if (isActive) {
                _remindersSortedLive.postValue(reminders)
            }
            sortJob = null
        }
    }

}
