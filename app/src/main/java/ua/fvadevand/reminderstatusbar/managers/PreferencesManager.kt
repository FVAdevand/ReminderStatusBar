package ua.fvadevand.reminderstatusbar.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class PreferencesManager(context: Context) {

    companion object {
        private const val PREF_NIGHT_MODE = "NIGHT_MODE"
        private const val PREF_REMINDER_SORT_FIELD = "REMINDER_SORT_FIELD"
        private const val PREF_REMINDER_SORT_ORDER_ASC = "REMINDER_SORT_ORDER_ASC"
        private const val DEFAULT_REMINDER_SORT_FIELD = Reminder.COLUMN_TITLE
        const val DEFAULT_REMINDER_SORT_ORDER_ASC = true
    }

    private val sharedPref: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    var reminderSortField
        @Reminder.SortFields get() = sharedPref.getString(
            PREF_REMINDER_SORT_FIELD,
            DEFAULT_REMINDER_SORT_FIELD
        )
            ?: DEFAULT_REMINDER_SORT_FIELD
        set(@Reminder.SortFields value) = sharedPref.edit {
            putString(
                PREF_REMINDER_SORT_FIELD,
                value
            )
        }


    var reminderSortOrderAsc
        get() = sharedPref.getBoolean(PREF_REMINDER_SORT_ORDER_ASC, DEFAULT_REMINDER_SORT_ORDER_ASC)
        set(value) = sharedPref.edit { putBoolean(PREF_REMINDER_SORT_ORDER_ASC, value) }

    var nightMode
        get() = sharedPref.getInt(PREF_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = sharedPref.edit { putInt(PREF_NIGHT_MODE, value) }

}