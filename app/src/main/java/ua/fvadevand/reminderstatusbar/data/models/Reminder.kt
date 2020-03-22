package ua.fvadevand.reminderstatusbar.data.models

import androidx.annotation.StringDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.fvadevand.reminderstatusbar.data.models.PeriodType.PeriodTypes
import ua.fvadevand.reminderstatusbar.data.models.Reminder.Companion.TABLE_NAME
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus.ReminderStatuses

@Entity(tableName = TABLE_NAME)
data class Reminder(
    @ColumnInfo(name = COLUMN_TITLE)
    var title: String,

    @ColumnInfo(name = COLUMN_TEXT)
    var text: String? = null,

    @ColumnInfo(name = COLUMN_ICON_RES_NAME)
    var iconName: String,

    @ColumnInfo(name = COLUMN_TIMESTAMP)
    var timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = COLUMN_STATUS)
    @ReminderStatuses
    var status: Int,

    @ColumnInfo(name = COLUMN_PERIOD_TYPE)
    @PeriodTypes
    var periodType: Int = PeriodType.ONE_TIME,

    @ColumnInfo(name = COLUMN_PERIOD)
    var period: Long = 0,

    @ColumnInfo(name = COLUMN_PERIOD_ACCEPT)
    var periodAccepted: Boolean = true
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id: Long = 0

    companion object {
        const val TABLE_NAME = "reminders"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TEXT = "text"
        const val COLUMN_ICON_RES_NAME = "icon_res_name"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_STATUS = "status"
        const val COLUMN_PERIOD_TYPE = "period_type"
        const val COLUMN_PERIOD = "repeat_period"
        const val COLUMN_PERIOD_ACCEPT = "period_accept"
    }

    @StringDef(
        COLUMN_TITLE,
        COLUMN_STATUS,
        COLUMN_TIMESTAMP
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class SortFields

}
