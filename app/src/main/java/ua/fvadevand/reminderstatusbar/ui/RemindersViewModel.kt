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
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.handlers.AppPref
import ua.fvadevand.reminderstatusbar.utils.AlarmUtils
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils
import ua.fvadevand.reminderstatusbar.utils.SortUtils
import java.util.Collections

class RemindersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReminderApp.instance.repository
    private val appPref = ReminderApp.instance.appPref
    private val _remindersSortedLive: MediatorLiveData<List<Reminder>> = MediatorLiveData()
    private val reminderSortOrderAscLive: MutableLiveData<Boolean> = MutableLiveData()
    private val reminderSortFieldLive: MutableLiveData<String> = MutableLiveData()
    private val remindersFromDb: LiveData<List<Reminder>> by lazy(LazyThreadSafetyMode.NONE) {
        repository.getAllLiveReminders()
    }
    var currentReminderId = Const.NEW_REMINDER_ID
    var nightMode
        get() = appPref.nightMode
        set(value) {
            appPref.nightMode = value
            AppCompatDelegate.setDefaultNightMode(value)
        }
    val remindersSortedLive: LiveData<List<Reminder>> by lazy(LazyThreadSafetyMode.NONE) {
        reminderSortFieldLive.postValue(appPref.reminderSortField)
        reminderSortOrderAscLive.postValue(appPref.reminderSortOrderAsc)
        _remindersSortedLive.addSource(remindersFromDb) {
            sortAndPostReminders()
        }
        _remindersSortedLive.addSource(reminderSortFieldLive) {
            sortAndPostReminders()
        }
        _remindersSortedLive.addSource(reminderSortOrderAscLive) {
            sortAndPostReminders()
        }
        _remindersSortedLive
    }

    fun getCurrentReminder(onSuccess: (Reminder?) -> Unit) =
        viewModelScope.launch {
            onSuccess(repository.getReminderById(currentReminderId))
        }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminder.id = repository.addReminder(reminder)
            if (reminder.timestamp > System.currentTimeMillis()) {
                NotificationUtils.cancel(getApplication(), reminder.id.hashCode())
                AlarmUtils.setAlarm(getApplication(), reminder)
            } else {
                NotificationUtils.showNotification(getApplication(), reminder)
                if (reminder.periodType > PeriodType.ONE_TIME) {
                    reminder.timestamp =
                        PeriodType.getNextAlarmTimeByType(reminder.periodType, reminder.timestamp)
                    AlarmUtils.setAlarm(getApplication(), reminder)
                    repository.editReminder(reminder)
                }
            }
        }
    }

    fun removeCurrentReminder() {
        viewModelScope.launch {
            val context: Context = getApplication()
            NotificationUtils.cancel(context, currentReminderId.hashCode())
            AlarmUtils.cancelAlarm(context, currentReminderId)
            repository.removeReminderById(currentReminderId)
        }
    }

    fun removeAllReminders() {
        viewModelScope.launch {
            repository.removeAllReminders()
        }
    }

    fun notifyCurrentReminder() {
        viewModelScope.launch {
            val context: Context = getApplication()
            repository.getReminderById(currentReminderId)?.let {
                AlarmUtils.cancelAlarm(context, currentReminderId)
                it.status = ReminderStatus.NOTIFYING
                it.timestamp = System.currentTimeMillis()
                repository.editReminder(it)
                NotificationUtils.showNotification(context, it)
            }
        }
    }

    fun setCurrentReminderStatusDone() {
        viewModelScope.launch {
            NotificationUtils.cancel(getApplication(), currentReminderId.hashCode())
            AlarmUtils.cancelAlarm(getApplication(), currentReminderId)
            repository.getReminderById(currentReminderId)?.let {
                it.status = ReminderStatus.DONE
                it.timestamp = System.currentTimeMillis()
                repository.editReminder(it)
            }
        }
    }

    fun setSortField(@Reminder.SortFields sortField: String) {
        if (sortField != reminderSortFieldLive.value) {
            appPref.reminderSortField = sortField
            reminderSortFieldLive.postValue(sortField)
        } else {
            val sortOrderAsc = reminderSortOrderAscLive.value
                ?: AppPref.DEFAULT_REMINDER_SORT_ORDER_ASC
            appPref.reminderSortOrderAsc = !sortOrderAsc
            reminderSortOrderAscLive.postValue(!sortOrderAsc)
        }
    }

    private fun sortAndPostReminders() {
        viewModelScope.launch(Dispatchers.IO) {
            val reminders = remindersFromDb.value ?: return@launch
            val sortField = reminderSortFieldLive.value ?: return@launch
            val sortOrderAsc = reminderSortOrderAscLive.value
                ?: AppPref.DEFAULT_REMINDER_SORT_ORDER_ASC
            val comparator = when (sortField) {
                Reminder.COLUMN_TITLE -> SortUtils.ReminderTitleComparator(sortOrderAsc)
                Reminder.COLUMN_STATUS -> SortUtils.ReminderStatusComparator(sortOrderAsc)
                Reminder.COLUMN_TIMESTAMP -> SortUtils.ReminderTimeComparator(sortOrderAsc)
                else -> throw IllegalArgumentException(
                    "SortField must be in" +
                            " @Reminder.SortFields, current value = $sortField "
                )
            }
            Collections.sort(reminders, comparator)
            _remindersSortedLive.postValue(reminders)
        }
    }
}
