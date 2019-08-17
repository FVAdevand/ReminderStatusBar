package ua.fvadevand.reminderstatusbar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.utils.AlarmUtils
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED != intent?.action) return
        val currentTimeInMillis = System.currentTimeMillis()
        GlobalScope.launch(Dispatchers.IO) {
            val reminders = ReminderApp.instance.repository.getRemindersForNotify()
            reminders.forEach { reminder ->
                if (reminder.timestamp > currentTimeInMillis) {
                    AlarmUtils.setAlarm(context, reminder)
                } else {
                    NotificationUtils.showNotification(context, reminder)
                }
            }
        }
    }
}