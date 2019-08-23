package ua.fvadevand.reminderstatusbar.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.Repository
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.utils.AlarmUtils
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils

class RemindersViewModel(application: Application) : AndroidViewModel(application) {

    var currentReminderId = Const.NEW_REMINDER_ID
    private val repository: Repository = ReminderApp.instance.repository
    val reminders: LiveData<List<Reminder>> by lazy(LazyThreadSafetyMode.NONE) {
        repository.getAllLiveReminders()
    }

    private fun getLiveReminderById(id: Long): LiveData<Reminder> {
        return repository.getLiveReminderById(id)
    }

    fun getLiveCurrentReminder(): LiveData<Reminder> {
        return getLiveReminderById(currentReminderId)
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            val id = repository.addReminder(reminder)
            reminder.id = id
            if (reminder.timestamp > System.currentTimeMillis()) {
                NotificationUtils.cancel(getApplication(), id.hashCode())
                AlarmUtils.setAlarm(getApplication(), reminder)
            } else {
                NotificationUtils.showNotification(getApplication(), reminder)
                if (reminder.periodType > PeriodType.ONE_TIME) {
                    val nextTimestamp = PeriodType.getNextAlarmTimeByType(reminder.periodType, reminder.timestamp)
                    reminder.timestamp = nextTimestamp
                    AlarmUtils.setAlarm(getApplication(), reminder)
                    repository.editReminder(reminder)
                }
            }
        }
    }

    fun removeCurrentReminder() {
        removeReminderById(currentReminderId)
    }

    fun removeAllReminders() {
        viewModelScope.launch {
            repository.removeAllReminders()
        }
    }

    fun notifyCurrentReminder() {
        notifyReminder(currentReminderId)
    }

    fun setCurrentReminderStatusDone() {
        viewModelScope.launch {
            NotificationUtils.cancel(getApplication(), currentReminderId.hashCode())
            AlarmUtils.cancelAlarm(getApplication(), currentReminderId)
            val reminder = repository.getReminderById(currentReminderId)
            reminder?.let {
                it.status = ReminderStatus.DONE
                it.timestamp = System.currentTimeMillis()
                repository.editReminder(it)
            }
        }
    }

    private fun removeReminderById(id: Long) {
        viewModelScope.launch {
            val context: Context = getApplication()
            NotificationUtils.cancel(context, id.hashCode())
            AlarmUtils.cancelAlarm(context, id)
            repository.removeReminderById(id)
        }
    }

    private fun notifyReminder(id: Long) {
        viewModelScope.launch {
            val context: Context = getApplication()
            val reminder = repository.getReminderById(id)
            reminder?.let {
                AlarmUtils.cancelAlarm(context, id)
                it.status = ReminderStatus.NOTIFYING
                it.timestamp = System.currentTimeMillis()
                repository.editReminder(it)
                NotificationUtils.showNotification(context, it)
            }
        }
    }
}
