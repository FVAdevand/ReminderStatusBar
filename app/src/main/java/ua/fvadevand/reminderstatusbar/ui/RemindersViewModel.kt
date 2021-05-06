package ua.fvadevand.reminderstatusbar.ui

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.managers.AlarmManager
import ua.fvadevand.reminderstatusbar.managers.NotificationManager
import ua.fvadevand.reminderstatusbar.managers.PreferencesManager
import ua.fvadevand.reminderstatusbar.utils.SortUtils

class RemindersViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationContext: Context get() = getApplication()
    private val repository = ReminderApp.getRepository()
    private val prefManager = PreferencesManager(applicationContext)
    private val alarmManager by lazy { AlarmManager(applicationContext) }
    private val notificationManager by lazy { NotificationManager(applicationContext) }
    private val reminderSortOrderAscFlow = MutableStateFlow(true)
    private val reminderSortFieldFlow = MutableStateFlow(Reminder.COLUMN_TIMESTAMP)
    private val remindersFromDb by lazy { repository.getAllRemindersFlow() }

    var nightMode
        get() = prefManager.nightMode
        set(value) {
            prefManager.nightMode = value
            AppCompatDelegate.setDefaultNightMode(value)
        }

    fun getRemindersSortedFlow(): SharedFlow<List<Reminder>> {
        val sortConditionFlow = reminderSortOrderAscFlow.combine(reminderSortFieldFlow, ::Pair)
        return remindersFromDb.combine(sortConditionFlow) { reminders, condition ->
            val (sortOrderAsc, sortField) = condition
            val comparator = when (sortField) {
                Reminder.COLUMN_TITLE -> SortUtils.ReminderTitleComparator(sortOrderAsc)
                Reminder.COLUMN_STATUS -> SortUtils.ReminderStatusComparator(sortOrderAsc)
                Reminder.COLUMN_TIMESTAMP -> SortUtils.ReminderTimeComparator(sortOrderAsc)
                else -> throw IllegalArgumentException(
                    "SortField must be in @Reminder.SortFields, current value = $sortField "
                )
            }
            reminders.sortedWith(comparator)
        }.flowOn(Dispatchers.IO)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)
    }

    fun getReminder(id: Long, onSuccess: (Reminder?) -> Unit) =
        viewModelScope.launch {
            onSuccess(repository.getReminderById(id))
        }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminder.periodAccepted = true
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
                    reminder.periodAccepted = false
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
            repository.getReminderById(id)?.let { reminder ->
                if (reminder.status == ReminderStatus.PERIODIC) {
                    reminder.periodAccepted = false
                } else {
                    alarmManager.cancelAlarm(id)
                    reminder.status = ReminderStatus.NOTIFYING
                    reminder.timestamp = System.currentTimeMillis()
                }
                repository.editReminder(reminder)
                notificationManager.showNotification(reminder)
            }
        }
    }

    fun setReminderStatusDone(id: Long) {
        viewModelScope.launch {
            notificationManager.cancelNotification(id.hashCode())
            repository.getReminderById(id)?.let { reminder ->
                if (reminder.status == ReminderStatus.PERIODIC) {
                    reminder.periodAccepted = true
                } else {
                    alarmManager.cancelAlarm(id)
                    reminder.status = ReminderStatus.DONE
                    reminder.timestamp = System.currentTimeMillis()
                }
                repository.editReminder(reminder)
            }
        }
    }

    fun pausePeriodicReminder(id: Long) {
        viewModelScope.launch {
            notificationManager.cancelNotification(id.hashCode())
            repository.getReminderById(id)?.let { reminder ->
                if (reminder.status == ReminderStatus.PERIODIC) {
                    reminder.periodAccepted = true
                    alarmManager.cancelAlarm(id)
                    reminder.status = ReminderStatus.PAUSED
                    repository.editReminder(reminder)
                }
            }
        }
    }

    fun restorePeriodicReminder(id: Long) {
        viewModelScope.launch {
            repository.getReminderById(id)?.let { reminder ->
                if (reminder.status == ReminderStatus.PAUSED) {
                    if (System.currentTimeMillis() > reminder.timestamp) {
                        reminder.timestamp = PeriodType.getNextAlarmTimeByType(
                            reminder.periodType,
                            reminder.timestamp
                        )
                    }
                    alarmManager.setAlarm(reminder)
                    reminder.status = ReminderStatus.PERIODIC
                    repository.editReminder(reminder)
                }
            }
        }
    }

    fun setSortField(@Reminder.SortFields sortField: String) {
        if (sortField != reminderSortFieldFlow.value) {
            prefManager.reminderSortField = sortField
            reminderSortFieldFlow.value = sortField
        } else {
            val sortOrderAsc = reminderSortOrderAscFlow.value
            prefManager.reminderSortOrderAsc = !sortOrderAsc
            reminderSortOrderAscFlow.value = !sortOrderAsc
        }
    }

}
