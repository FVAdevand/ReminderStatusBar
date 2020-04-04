package ua.fvadevand.reminderstatusbar.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.ReminderApp
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.managers.AlarmManager
import ua.fvadevand.reminderstatusbar.managers.NotificationManager

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val ACTION_SHOW_REMINDER =
            "ua.fvadevand.reminderstatusbar.ACTION_SHOW_REMINDER"
        private const val ACTION_DONE = "ua.fvadevand.reminderstatusbar.ACTION_DONE"
        private const val ACTION_DELETE = "ua.fvadevand.reminderstatusbar.ACTION_DELETE"
        private const val EXTRA_REMINDER_ID = "REMINDER_ID"

        fun getNotifyIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                Intent(ACTION_SHOW_REMINDER)
                    .setPackage(context.packageName)
                    .putExtra(EXTRA_REMINDER_ID, reminderId),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun getDoneIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                Intent(ACTION_DONE)
                    .setPackage(context.packageName)
                    .putExtra(EXTRA_REMINDER_ID, reminderId),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun getDeleteIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                Intent(ACTION_DELETE)
                    .setPackage(context.packageName)
                    .putExtra(EXTRA_REMINDER_ID, reminderId),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val reminderId =
            intent?.getLongExtra(EXTRA_REMINDER_ID, Const.NEW_REMINDER_ID) ?: Const.NEW_REMINDER_ID
        val alarmManager = AlarmManager(context)
        val notificationManager = NotificationManager(context)
        val repository = ReminderApp.getRepository()
        when (intent?.action) {
            ACTION_SHOW_REMINDER -> {
                GlobalScope.launch(Dispatchers.Main) {
                    repository.getReminderById(reminderId)?.let { reminder ->
                        notificationManager.showNotification(reminder)
                        if (reminder.status == ReminderStatus.PERIODIC) {
                            val nextAlarmTime =
                                PeriodType.getNextAlarmTimeByType(
                                    reminder.periodType,
                                    reminder.timestamp
                                )
                            reminder.timestamp = nextAlarmTime
                            reminder.periodAccepted = false
                            alarmManager.setAlarm(reminder)
                        } else {
                            reminder.status = ReminderStatus.NOTIFYING
                        }
                        repository.editReminder(reminder)
                    }
                }
            }

            ACTION_DONE -> {
                notificationManager.cancelNotification(reminderId.hashCode())
                GlobalScope.launch(Dispatchers.IO) {
                    repository.getReminderById(reminderId)?.let { reminder ->
                        if (reminder.status == ReminderStatus.PERIODIC) {
                            reminder.periodAccepted = true
                        } else {
                            reminder.status = ReminderStatus.DONE
                        }
                        repository.editReminder(reminder)
                    }
                }
            }

            ACTION_DELETE -> {
                notificationManager.cancelNotification(reminderId.hashCode())
                alarmManager.cancelAlarm(reminderId)
                GlobalScope.launch(Dispatchers.IO) {
                    repository.deleteReminderById(reminderId)
                }
            }
        }
    }

}