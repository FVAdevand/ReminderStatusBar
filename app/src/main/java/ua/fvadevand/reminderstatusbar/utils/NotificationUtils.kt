package ua.fvadevand.reminderstatusbar.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.receivers.NotificationReceiver

object NotificationUtils {

    private const val TAG = "NotificationUtils"
    private const val VERSION = 1
    private const val REMINDER_CHANNEL_ID = "reminder_channel_$VERSION"

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun registerNotificationChannels(context: Context) {
        val name = context.getString(R.string.reminder_channel_name)
        val description = context.getString(R.string.reminder_channel_description)
        val channel = NotificationChannel(REMINDER_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
        channel.description = description
        channel.enableLights(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, reminder: Reminder) {
        val iconResId = reminder.iconResId
        val reminderId = reminder.id
        val nBuilder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setContentTitle(reminder.title)
                .setSmallIcon(iconResId)
                .setColor(context.getColor(R.color.colorAccent))
                .setOngoing(true)
                .setShowWhen(true)
                .setAutoCancel(true)
                .addAction(getDismissAction(context, reminderId))
                .addAction(getDeleteAction(context, reminderId))
        reminder.text?.let { nBuilder.setContentText(it) }
        val iconDrawable = context.getDrawable(iconResId)
        iconDrawable?.let {
            it.setColorFilter(context.getColor(R.color.colorReminderIcons), PorterDuff.Mode.SRC_IN)
            val largeIcon = getBitmap(it)
            nBuilder.setLargeIcon(largeIcon)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(reminderId.hashCode(), nBuilder.build())
    }

    fun cancel(context: Context, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(id)
    }

    private fun getBitmap(drawable: Drawable?): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        if (drawable != null) {
            var width = drawable.intrinsicWidth
            width = if (width > 0) width else 1
            var height = drawable.intrinsicHeight
            height = if (height > 0) height else 1

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
        return null
    }

    private fun getDismissAction(context: Context, reminderId: Long): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(R.drawable.ic_action_cancel,
                context.getString(R.string.notification_action_dismiss),
                NotificationReceiver.getDismissIntent(context, reminderId))
                .build()
    }

    private fun getDeleteAction(context: Context, reminderId: Long): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(R.drawable.ic_action_delete,
                context.getString(R.string.notification_action_delete),
                NotificationReceiver.getDeleteIntent(context, reminderId))
                .build()
    }
}