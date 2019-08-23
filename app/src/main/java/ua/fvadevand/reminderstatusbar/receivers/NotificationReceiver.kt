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
import ua.fvadevand.reminderstatusbar.utils.AlarmUtils
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, Const.NEW_REMINDER_ID)
        when (action) {
            ACTION_SHOW_REMINDER -> {
                GlobalScope.launch(Dispatchers.Main) {
                    val reminder = ReminderApp.instance.repository.getReminderById(reminderId)
                    reminder?.let {
                        NotificationUtils.showNotification(context, it)
                        if (it.status == ReminderStatus.PERIODIC) {
                            val nextTimestamp = PeriodType.getNextAlarmTimeByType(it.periodType, it.timestamp)
                            it.timestamp = nextTimestamp
                            if (nextTimestamp > System.currentTimeMillis()) {
                                AlarmUtils.setAlarm(context, reminder)
                            }
                        } else {
                            it.status = ReminderStatus.NOTIFYING
                        }
                        ReminderApp.instance.repository.editReminder(it)
                    }
                }
            }
            ACTION_DONE -> {
                NotificationUtils.cancel(context, reminderId.hashCode())
                GlobalScope.launch(Dispatchers.IO) {
                    ReminderApp.instance.repository.updateStatus(reminderId, ReminderStatus.DONE)
                }
            }
            ACTION_DELETE -> {
                NotificationUtils.cancel(context, reminderId.hashCode())
                GlobalScope.launch(Dispatchers.IO) {
                    ReminderApp.instance.repository.removeReminderById(reminderId)
                }
            }
        }
    }

    companion object {
        private const val ACTION_SHOW_REMINDER = "ua.fvadevand.reminderstatusbar.ACTION_SHOW_REMINDER"
        private const val ACTION_DONE = "ua.fvadevand.reminderstatusbar.ACTION_DONE"
        private const val ACTION_DELETE = "ua.fvadevand.reminderstatusbar.ACTION_DELETE"
        private const val EXTRA_REMINDER_ID = "REMINDER_ID"

        fun getNotifyIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(context,
                    reminderId.hashCode(),
                    Intent(ACTION_SHOW_REMINDER)
                            .setPackage(context.packageName)
                            .putExtra(EXTRA_REMINDER_ID, reminderId),
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun getDoneIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(context,
                    reminderId.hashCode(),
                    Intent(ACTION_DONE)
                            .setPackage(context.packageName)
                            .putExtra(EXTRA_REMINDER_ID, reminderId),
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun getDeleteIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(context,
                    reminderId.hashCode(),
                    Intent(ACTION_DELETE)
                            .setPackage(context.packageName)
                            .putExtra(EXTRA_REMINDER_ID, reminderId),
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}