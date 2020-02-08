package ua.fvadevand.reminderstatusbar.data.models

import androidx.annotation.IntDef
import androidx.annotation.StringRes
import ua.fvadevand.reminderstatusbar.R
import java.util.Calendar

object PeriodType {

    @IntDef(
        ONE_TIME,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class PeriodTypes

    const val ONE_TIME = 0
    const val DAILY = 1
    const val WEEKLY = 2
    const val MONTHLY = 3
    const val YEARLY = 4

    @StringRes
    fun getPeriodTypeStringResId(@PeriodTypes type: Int) =
        when (type) {
            ONE_TIME -> R.string.alarm_dialog_period_without
            DAILY -> R.string.alarm_dialog_period_daily
            WEEKLY -> R.string.alarm_dialog_period_weekly
            MONTHLY -> R.string.alarm_dialog_period_monthly
            YEARLY -> R.string.alarm_dialog_period_yearly
            else -> throw IllegalArgumentException("Type must be from @PeriodTypes, current type = $type")
        }

    fun getPeriodTypes() = listOf(
        ONE_TIME,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    )

    fun getNextAlarmTimeByType(@PeriodTypes type: Int, currentTime: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        when (type) {
            ONE_TIME -> calendar.add(Calendar.DAY_OF_MONTH, 0)
            DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            WEEKLY -> calendar.add(Calendar.DAY_OF_MONTH, 7)
            MONTHLY -> calendar.add(Calendar.MONTH, 1)
            YEARLY -> calendar.add(Calendar.YEAR, 1)
            else -> throw IllegalArgumentException("Type must be from @PeriodTypes, current type = $type")
        }
        return calendar.timeInMillis
    }
}