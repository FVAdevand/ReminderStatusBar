package ua.fvadevand.reminderstatusbar.utilities;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import ua.fvadevand.reminderstatusbar.R;

public class ReminderDateUtils {

    private ReminderDateUtils() {
        //no instance
    }

    public static String formatTime(Context context, Calendar calendar) {

        String formatTimeString;
        if (DateFormat.is24HourFormat(context)) {
            formatTimeString = context.getString(R.string.utils_format_time_24h);
        } else {
            formatTimeString = context.getString(R.string.utils_format_time_12h);
        }
        Format timeFormat = new SimpleDateFormat(formatTimeString, Locale.getDefault());
        return timeFormat.format(calendar.getTime());
    }

    public static String formatFullDate(Context context, Calendar calendar) {
        Format dateFormat = new SimpleDateFormat(context.getString(R.string.utils_format_full_date), Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static String formatShortDate(Context context, Calendar calendar) {
        Format dateFormat = new SimpleDateFormat(context.getString(R.string.utils_format_short_date), Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static long getUtcTimeInMillis(Calendar calendar) {
        long offset = calendar.getTimeZone().getOffset(calendar.getTimeInMillis());
        return calendar.getTimeInMillis() - offset;
    }

    public static long getLocalTimeInMillis(long utcTimeInMillis) {
        long offset = TimeZone.getDefault().getOffset(utcTimeInMillis);
        return utcTimeInMillis + offset;
    }

    public static String getNotificationTime(Context context, Calendar calendar) {
        Calendar currentCalendar = Calendar.getInstance();
        if (currentCalendar.after(calendar) || currentCalendar.equals(calendar)) {
            return context.getResources().getString(R.string.utils_date_now);
        }
        int currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        if (currentDayOfYear == dayOfYear) {
            return context.getResources().getString(R.string.utils_date_today, formatTime(context, calendar));
        } else if (currentDayOfYear == dayOfYear - 1) {
            return context.getResources().getString(R.string.utils_date_tomorrow, formatTime(context, calendar));
        } else {
            return formatNotificationTime(context, calendar) + " " + formatTime(context, calendar);
        }
    }

    private static String formatNotificationTime(Context context, Calendar calendar) {
        Format dateFormat = new SimpleDateFormat(context.getString(R.string.utils_format_notification_time), Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
