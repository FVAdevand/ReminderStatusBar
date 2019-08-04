package ua.fvadevand.reminderstatusbar.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ua.fvadevand.reminderstatusbar.data.AppRepository
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class RemindersViewModel : ViewModel() {

    private val repository: AppRepository = AppRepository.instance
    val reminders: LiveData<List<Reminder>>

    init {
        reminders = repository.getReminders()
    }

    fun getReminderById(id: Long): LiveData<Reminder> {
        return repository.getReminderById(id)
    }

    fun insertReminder(reminder: Reminder) {
        repository.insertReminder(reminder) { Log.i(TAG, "insertSuccess: count=$it") }
    }

    fun deleteReminder(reminder: Reminder) {
        repository.deleteReminder(reminder)
    }

    fun clearDb() {
        repository.clearDb()
    }

    companion object {
        private const val TAG = "RemindersViewModel"
    }
}
