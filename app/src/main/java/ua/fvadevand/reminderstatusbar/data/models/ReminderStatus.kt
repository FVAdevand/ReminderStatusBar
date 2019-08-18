package ua.fvadevand.reminderstatusbar.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import ua.fvadevand.reminderstatusbar.R

object ReminderStatus {

    @IntDef(
            DONE,
            NOTIFYING,
            DELAYED
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class ReminderStatuses

    const val DONE = 1
    const val NOTIFYING = 2
    const val DELAYED = 3

    @DrawableRes
    fun getIconResIdByStatus(@ReminderStatuses status: Int): Int {
        return when (status) {
            DONE -> R.drawable.ic_status_done
            NOTIFYING -> R.drawable.ic_status_notifying
            DELAYED -> R.drawable.ic_status_delayed
            else -> R.drawable.ic_status_done
        }
    }
}