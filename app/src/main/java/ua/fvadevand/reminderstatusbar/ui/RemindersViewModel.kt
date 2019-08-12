package ua.fvadevand.reminderstatusbar.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.app.ReminderApp
import ua.fvadevand.reminderstatusbar.data.Repository
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class RemindersViewModel : ViewModel() {

    private val repository: Repository = ReminderApp.instance.repository
    val reminders: LiveData<List<Reminder>> by lazy(LazyThreadSafetyMode.NONE) {
        repository.getAllReminders()
    }

    fun getReminderById(id: Long): LiveData<Reminder> {
        return repository.getReminderById(id)
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.addReminder(reminder)
        }
    }

    fun removeReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.removeReminder(reminder)
        }
    }
}
