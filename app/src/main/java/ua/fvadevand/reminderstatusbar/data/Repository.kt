package ua.fvadevand.reminderstatusbar.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.fvadevand.reminderstatusbar.data.database.ReminderDao
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class Repository(
        private val reminderDao: ReminderDao
) {
    fun getAllReminders(): LiveData<List<Reminder>> {
        return reminderDao.getAll()
    }

    fun getReminderById(id: Long): LiveData<Reminder> {
        return reminderDao.getById(id)
    }

    suspend fun addReminder(reminder: Reminder) {
        runInIOThread {
            reminderDao.insert(reminder)
        }
    }

    suspend fun editReminder(reminder: Reminder) {
        runInIOThread {
            reminderDao.update(reminder)
        }
    }

    suspend fun removeReminder(reminder: Reminder) {
        runInIOThread {
            reminderDao.delete(reminder)
        }
    }

    private suspend fun runInIOThread(block: () -> Unit) {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}