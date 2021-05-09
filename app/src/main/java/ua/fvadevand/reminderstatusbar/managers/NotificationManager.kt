package ua.fvadevand.reminderstatusbar.managers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.receivers.NotificationReceiver
import ua.fvadevand.reminderstatusbar.ui.MainActivity
import ua.fvadevand.reminderstatusbar.utils.toResId

class NotificationManager(private val context: Context) {

    private val manager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    companion object {
        private const val VERSION = 1
        private const val REMINDER_CHANNEL_ID = "reminder_channel_$VERSION"
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun registerNotificationChannels() {
        val name = context.getString(R.string.reminder_channel_name)
        val description = context.getString(R.string.reminder_channel_description)
        val channel =
            NotificationChannel(REMINDER_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
        channel.description = description
        channel.enableLights(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        manager.createNotificationChannel(channel)
    }

    fun showNotification(reminder: Reminder) {
        val iconResId = context.toResId(reminder.iconName)
        val reminderId = reminder.id
        val nBuilder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setContentTitle(reminder.title)
            .setSmallIcon(iconResId)
            .setColor(context.getColor(R.color.colorSecondary))
            .setOngoing(true)
            .setShowWhen(true)
            .setContentIntent(MainActivity.getOpenIntent(context, reminderId))
            .addAction(getDoneAction(reminderId))
        reminder.text?.let {
            nBuilder.setContentText(it)
                .setStyle(NotificationCompat.BigTextStyle().bigText(it))
        }
        if (reminder.status != ReminderStatus.PERIODIC) {
            nBuilder.addAction(getDeleteAction(reminderId))
        }
        val iconDrawable = ContextCompat.getDrawable(context, iconResId)
        iconDrawable?.let {
            it.setTint(context.getColor(R.color.colorReminderIcons))
            val largeIcon = getBitmap(it)
            nBuilder.setLargeIcon(largeIcon)
        }
        manager.notify(reminderId.hashCode(), nBuilder.build())
    }

    fun cancelNotification(id: Int) = manager.cancel(id)

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

    private fun getDoneAction(reminderId: Long): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.ic_action_done,
            context.getString(R.string.notification_action_done),
            NotificationReceiver.getDoneIntent(context, reminderId)
        ).build()
    }

    private fun getDeleteAction(reminderId: Long): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            R.drawable.ic_action_delete,
            context.getString(R.string.notification_action_delete),
            NotificationReceiver.getDeleteIntent(context, reminderId)
        ).build()
    }

}