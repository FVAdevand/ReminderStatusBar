package ua.fvadevand.reminderstatusbar

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ua.fvadevand.reminderstatusbar.data.Repository
import ua.fvadevand.reminderstatusbar.data.database.AppDatabase
import ua.fvadevand.reminderstatusbar.handlers.AppPref
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils
import ua.fvadevand.reminderstatusbar.utils.Utils

class ReminderApp : Application() {

    lateinit var repository: Repository
        private set

    lateinit var appPref: AppPref
        private set

    companion object {
        lateinit var instance: ReminderApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appPref = AppPref(applicationContext)
        val db = AppDatabase.getDatabase(applicationContext)
        repository = Repository(db.reminderDao())
        if (Utils.isAndroidO()) {
            NotificationUtils.registerNotificationChannels(applicationContext)
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}
