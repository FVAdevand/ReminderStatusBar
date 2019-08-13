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

    fun getLiveReminderById(id: Long): LiveData<Reminder> {
        return reminderDao.getLiveById(id)
    }

    suspend fun getReminderById(id: Long): Reminder? {
        return withContext(Dispatchers.IO) {
            reminderDao.getById(id)
        }
    }

    suspend fun addReminder(reminder: Reminder): Long {
        return withContext(Dispatchers.IO) {
            reminderDao.insert(reminder)
        }
    }

    suspend fun editReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
    }

    suspend fun removeReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.delete(reminder)
        }
    }

    suspend fun removeAllReminders() {
        withContext(Dispatchers.IO) {
            reminderDao.deleteAll()
        }
    }
}