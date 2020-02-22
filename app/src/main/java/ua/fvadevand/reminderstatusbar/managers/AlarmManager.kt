package ua.fvadevand.reminderstatusbar.managers

import android.app.AlarmManager
import android.content.Context
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.receivers.NotificationReceiver

class AlarmManager(private val context: Context) {

    private val manager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    fun setAlarm(reminder: Reminder) {
        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.timestamp,
            NotificationReceiver.getNotifyIntent(context, reminder.id)
        )
    }

    fun cancelAlarm(reminderId: Long) {
        manager.cancel(NotificationReceiver.getNotifyIntent(context, reminderId))
    }

}