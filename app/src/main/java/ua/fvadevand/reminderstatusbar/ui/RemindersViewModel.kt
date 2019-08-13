package ua.fvadevand.reminderstatusbar.ui

import android.app.Application
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
        repository.getAllReminders()
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

    fun removeReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.removeReminder(reminder)
        }
    }

    fun removeAllReminders() {
        viewModelScope.launch {
            repository.removeAllReminders()
        }
    }
}
