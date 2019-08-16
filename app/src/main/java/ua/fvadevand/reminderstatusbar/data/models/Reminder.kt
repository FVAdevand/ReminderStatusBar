package ua.fvadevand.reminderstatusbar.data.models

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.data.converters.IconResTypeConverter

@Entity(tableName = Const.TABLE_NAME_REMINDERS)
data class Reminder(
        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "text")
        var text: String? = null,

        @ColumnInfo(name = "icon_res_name")
        @TypeConverters(IconResTypeConverter::class)
        @DrawableRes
        var iconResId: Int,

        @ColumnInfo(name = "timestamp")
        var timestamp: Long,

        @ColumnInfo(name = "notify_status")
        var notify: Boolean

) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
