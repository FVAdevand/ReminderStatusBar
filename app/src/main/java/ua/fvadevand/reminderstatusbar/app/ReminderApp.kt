package ua.fvadevand.reminderstatusbar.app

import android.app.Application
import ua.fvadevand.reminderstatusbar.data.Repository
import ua.fvadevand.reminderstatusbar.data.database.AppDatabase

class ReminderApp : Application() {

    lateinit var repository: Repository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        val db = AppDatabase.getDatabase(applicationContext)
        repository = Repository(db.reminderDao())
    }

    companion object {
        lateinit var instance: ReminderApp
            private set
    }
}
