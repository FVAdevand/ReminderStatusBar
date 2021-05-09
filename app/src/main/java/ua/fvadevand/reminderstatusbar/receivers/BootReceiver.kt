package ua.fvadevand.reminderstatusbar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.managers.AlarmManager
import ua.fvadevand.reminderstatusbar.managers.NotificationManager

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (Intent.ACTION_BOOT_COMPLETED != action && Intent.ACTION_MY_PACKAGE_REPLACED != action) return
        val now = System.currentTimeMillis()
        ReminderApp.applicationScope.launch(Dispatchers.IO) {
            val alarmManager = AlarmManager(context)
            val repository = ReminderApp.getRepository()
            val notificationManager = NotificationManager(context)
            val reminders = repository.getRemindersForNotify()
            reminders.forEach { reminder ->
                if (!reminder.periodAccepted) {
                    notificationManager.showNotification(reminder)
                }
                if (reminder.timestamp > now) {
                    alarmManager.setAlarm(reminder)
                } else {
                    notificationManager.showNotification(reminder)
                    if (reminder.status == ReminderStatus.PERIODIC) {
                        reminder.timestamp = PeriodType.getNextAlarmTimeByType(
                            reminder.periodType,
                            reminder.timestamp
                        )
                        reminder.periodAccepted = false
                        alarmManager.setAlarm(reminder)
                    } else {
                        reminder.status = ReminderStatus.NOTIFYING
                    }
                    repository.editReminder(reminder)
                }
            }
        }
    }
}