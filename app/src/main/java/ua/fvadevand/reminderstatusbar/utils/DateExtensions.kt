package ua.fvadevand.reminderstatusbar.utils

import android.content.Context
import android.text.format.DateFormat
import ua.fvadevand.reminderstatusbar.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun Context?.formatTime(calendar: Calendar): String {
    if (this == null) return "null"
    val resId =
        if (DateFormat.is24HourFormat(this)) R.string.utils_format_time_24h else R.string.utils_format_time_12h
    val timeFormat = SimpleDateFormat(getString(resId), Locale.getDefault())
    return timeFormat.format(calendar.time)
}

fun Context?.formatFullDate(calendar: Calendar): String {
    if (this == null) return "null"
    val dateFormat =
        SimpleDateFormat(getString(R.string.utils_format_full_date), Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun Context?.formatShortDate(calendar: Calendar): String {
    if (this == null) return "null"
    val dateFormat =
        SimpleDateFormat(getString(R.string.utils_format_short_date), Locale.getDefault())
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

fun Context?.getNotificationTime(timeInMillis: Long): String {
    if (this == null) return "null"
    if (timeInMillis <= System.currentTimeMillis()) {
        return resources.getString(R.string.utils_date_now)
    }
    val notifCalendar = Calendar.getInstance()
    notifCalendar.timeInMillis = timeInMillis
    val currentCalendar = Calendar.getInstance()
    val currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR)
    val dayOfYear = notifCalendar.get(Calendar.DAY_OF_YEAR)
    return when (currentDayOfYear) {
        dayOfYear -> resources.getString(
            R.string.utils_date_today,
            formatTime(notifCalendar)
        )

        dayOfYear - 1 -> resources.getString(
            R.string.utils_date_tomorrow,
            formatTime(notifCalendar)
        )
        else -> formatNotificationTime(notifCalendar) + " " + formatTime(notifCalendar)
    }
}

private fun Context.formatNotificationTime(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat(
        getString(R.string.utils_format_notification_time),
        Locale.getDefault()
    )
    return dateFormat.format(calendar.time)
}
