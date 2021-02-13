package ua.fvadevand.reminderstatusbar.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.fvadevand.reminderstatusbar.data.database.ReminderDao
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class Repository(private val reminderDao: ReminderDao) {

    fun getAllRemindersFlow() = reminderDao.getAllFlow()

    suspend fun getRemindersForNotify(): List<Reminder> {
        return withContext(Dispatchers.IO) {
            reminderDao.getRemindersForNotify()
        }
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

    suspend fun deleteReminderById(reminderId: Long) {
        withContext(Dispatchers.IO) {
            reminderDao.deleteById(reminderId)
        }
    }

}