package ua.fvadevand.reminderstatusbar.handlers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class AppPref(
    context: Context
) {

    private val sharedPref: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Reminder.SortFields
    var reminderSortField: String
        get() = sharedPref.getString(PREF_REMINDER_SORT_FIELD, DEFAULT_REMINDER_SORT_FIELD)
            ?: DEFAULT_REMINDER_SORT_FIELD
        set(value) {
            sharedPref.edit().putString(PREF_REMINDER_SORT_FIELD, value).apply()
        }

    var reminderSortOrderAsc
        get() = sharedPref.getBoolean(PREF_REMINDER_SORT_ORDER_ASC, DEFAULT_REMINDER_SORT_ORDER_ASC)
        set(value) {
            sharedPref.edit().putBoolean(PREF_REMINDER_SORT_ORDER_ASC, value).apply()
        }

    companion object {
        private const val PREF_REMINDER_SORT_FIELD = "REMINDER_SORT_FIELD"
        private const val PREF_REMINDER_SORT_ORDER_ASC = "REMINDER_SORT_ORDER_ASC"
        private const val DEFAULT_REMINDER_SORT_FIELD = Reminder.COLUMN_TITLE
        const val DEFAULT_REMINDER_SORT_ORDER_ASC = true
    }
}