package ua.fvadevand.reminderstatusbar.utils

import android.app.AlarmManager
import android.content.Context
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.receivers.NotificationReceiver


object AlarmUtils {

    fun setAlarm(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.timestamp,
                NotificationReceiver.getNotifyIntent(context, reminder.id))
    }
}