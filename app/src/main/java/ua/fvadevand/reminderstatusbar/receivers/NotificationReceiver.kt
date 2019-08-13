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
import ua.fvadevand.reminderstatusbar.utils.NotificationUtils

private const val ACTION_SHOW_REMINDER = "ua.fvadevand.reminderstatusbar.ACTION_SHOW_REMINDER"
private const val ACTION_DISMISS = "ua.fvadevand.reminderstatusbar.ACTION_DISMISS"
private const val EXTRA_REMINDER_ID = "REMINDER_ID"

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val reminderId: Long
        when (action) {
            ACTION_SHOW_REMINDER -> {
                reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, Const.NEW_REMINDER_ID)
                if (reminderId != Const.NEW_REMINDER_ID) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val reminder = ReminderApp.instance.repository.getReminderById(reminderId)
                        reminder?.let {
                            NotificationUtils.showNotification(context, it)
                        }
                    }
                }
            }
            ACTION_DISMISS -> {
                reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, 0)
                NotificationUtils.cancel(context, reminderId.hashCode())
            }
        }
    }

    companion object {
        fun getNotifyIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(context,
                    reminderId.hashCode(),
                    Intent(ACTION_SHOW_REMINDER)
                            .setPackage(context.packageName)
                            .putExtra(EXTRA_REMINDER_ID, reminderId),
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun getDismissIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getBroadcast(context,
                    reminderId.hashCode(),
                    Intent(ACTION_DISMISS)
                            .setPackage(context.packageName)
                            .putExtra(EXTRA_REMINDER_ID, reminderId),
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}