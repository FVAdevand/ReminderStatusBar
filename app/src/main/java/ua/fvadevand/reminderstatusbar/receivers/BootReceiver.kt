package ua.fvadevand.reminderstatusbar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.utils.AlarmUtils
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (Intent.ACTION_BOOT_COMPLETED != action && Intent.ACTION_MY_PACKAGE_REPLACED != action) return
        val currentTimeInMillis = System.currentTimeMillis()
        GlobalScope.launch(Dispatchers.IO) {
            val reminders = ReminderApp.instance.repository.getRemindersForNotify()
            reminders.forEach { reminder ->
                if (reminder.timestamp > currentTimeInMillis) {
                    AlarmUtils.setAlarm(context, reminder)
                } else {
                    ReminderApp.instance.repository.updateStatus(reminder.id, ReminderStatus.NOTIFYING)
                    NotificationUtils.showNotification(context, reminder)
                }
            }
        }
    }
}