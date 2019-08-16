package ua.fvadevand.reminderstatusbar.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.Repository
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.utils.AlarmUtils
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils

class RemindersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = ReminderApp.instance.repository
    val reminders: LiveData<List<Reminder>> by lazy(LazyThreadSafetyMode.NONE) {
        repository.getAllLiveReminders()
    }

    fun getLiveReminderById(id: Long): LiveData<Reminder> {
        return repository.getLiveReminderById(id)
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            val id = repository.addReminder(reminder)
            reminder.id = id
            if (reminder.timestamp <= System.currentTimeMillis()) {
                NotificationUtils.showNotification(getApplication(), reminder)
            } else {
                AlarmUtils.setAlarm(getApplication(), reminder)
            }
        }
    }

    fun removeReminderById(id: Long) {
        viewModelScope.launch {
            val context: Context = getApplication()
            NotificationUtils.cancel(context, id.hashCode())
            AlarmUtils.cancelAlarm(context, id)
            repository.removeReminderById(id)
        }
    }

    fun removeAllReminders() {
        viewModelScope.launch {
            repository.removeAllReminders()
        }
    }

    fun notifyReminder(id: Long) {
        viewModelScope.launch {
            val context: Context = getApplication()
            val reminder = repository.getReminderById(id)
            reminder?.let {
                NotificationUtils.showNotification(context, it)
                AlarmUtils.cancelAlarm(context, id)
                it.notify = true
                it.timestamp = System.currentTimeMillis()
                repository.editReminder(it)
            }
        }
    }
}
