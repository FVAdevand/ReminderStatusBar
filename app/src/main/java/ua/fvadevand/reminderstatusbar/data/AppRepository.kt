package ua.fvadevand.reminderstatusbar.data

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import ua.fvadevand.reminderstatusbar.app.ReminderApp
import ua.fvadevand.reminderstatusbar.data.database.AppDatabase
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.utilities.AppExecutors

class AppRepository private constructor(
        private val db: AppDatabase,
        private val executors: AppExecutors) {

    private val mUiHandler: Handler = Handler(Looper.getMainLooper())

    fun getReminders() = db.reminderDao().getAll()

    fun getReminderById(id: Long): LiveData<Reminder> {
        return db.reminderDao().getById(id)
    }

    fun insertReminder(reminder: Reminder, listener: (Long) -> Unit) {
        executors.diskIO.execute {
            val id = db.reminderDao().insert(reminder)
            mUiHandler.post {
                listener(id)
            }
        }
    }

    fun updateReminder(reminder: Reminder) {
        executors.diskIO.execute { db.reminderDao().update(reminder) }
    }

    fun deleteReminder(reminder: Reminder) {
        executors.diskIO.execute { db.reminderDao().delete(reminder) }
    }

    fun clearDb() {
        executors.diskIO.execute { db.clearAllTables() }
    }

    companion object {
        val instance: AppRepository by lazy { AppRepository(ReminderApp.instance.db, AppExecutors.instance) }
    }
}
