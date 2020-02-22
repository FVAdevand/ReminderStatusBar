package ua.fvadevand.reminderstatusbar

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ua.fvadevand.reminderstatusbar.data.Repository
import ua.fvadevand.reminderstatusbar.data.database.AppDatabase
import ua.fvadevand.reminderstatusbar.managers.NotificationManager
import ua.fvadevand.reminderstatusbar.managers.PreferencesManager
import ua.fvadevand.reminderstatusbar.utils.isAndroidO

class ReminderApp : Application() {

    private lateinit var repository: Repository

    companion object {
        private lateinit var instance: ReminderApp

        fun getRepository() = instance.repository
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val db = AppDatabase.getDatabase(applicationContext)
        repository = Repository(db.reminderDao())
        if (isAndroidO()) {
            NotificationManager(applicationContext).registerNotificationChannels()
        }

        AppCompatDelegate.setDefaultNightMode(PreferencesManager(applicationContext).nightMode)
    }

}
