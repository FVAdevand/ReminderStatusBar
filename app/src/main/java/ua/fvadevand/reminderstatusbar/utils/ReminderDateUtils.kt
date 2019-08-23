package ua.fvadevand.reminderstatusbar.utils

import android.content.Context
import android.text.format.DateFormat
import ua.fvadevand.reminderstatusbar.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object ReminderDateUtils {

    fun formatTime(context: Context, calendar: Calendar): String {
        val resId = if (DateFormat.is24HourFormat(context)) R.string.utils_format_time_24h else R.string.utils_format_time_12h
        val timeFormat = SimpleDateFormat(context.getString(resId), Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

    fun formatFullDate(context: Context, calendar: Calendar): String {
        val dateFormat = SimpleDateFormat(context.getString(R.string.utils_format_full_date), Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun formatShortDate(context: Context, calendar: Calendar): String {
        val dateFormat = SimpleDateFormat(context.getString(R.string.utils_format_short_date), Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getUtcTimeInMillis(calendar: Calendar): Long {
        val offset = calendar.timeZone.getOffset(calendar.timeInMillis).toLong()
        return calendar.timeInMillis - offset
    }

    fun getLocalTimeInMillis(utcTimeInMillis: Long): Long {
        val offset = TimeZone.getDefault().getOffset(utcTimeInMillis).toLong()
        return utcTimeInMillis + offset
    }

    fun getNotificationTime(context: Context, timeInMillis: Long): String {
        if (timeInMillis <= System.currentTimeMillis()) {
            return context.resources.getString(R.string.utils_date_now)
        }
        val notifCalendar = Calendar.getInstance()
        notifCalendar.timeInMillis = timeInMillis
        val currentCalendar = Calendar.getInstance()
        val currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR)
        val dayOfYear = notifCalendar.get(Calendar.DAY_OF_YEAR)
        return when (currentDayOfYear) {
            dayOfYear -> context.resources.getString(R.string.utils_date_today, formatTime(context, notifCalendar))
            dayOfYear - 1 -> context.resources.getString(R.string.utils_date_tomorrow, formatTime(context, notifCalendar))
            else -> formatNotificationTime(context, notifCalendar) + " " + formatTime(context, notifCalendar)
        }
    }

    private fun formatNotificationTime(context: Context, calendar: Calendar): String {
        val dateFormat = SimpleDateFormat(context.getString(R.string.utils_format_notification_time), Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
