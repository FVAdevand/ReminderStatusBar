package ua.fvadevand.reminderstatusbar.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.fvadevand.reminderstatusbar.Const

@Entity(tableName = Const.TABLE_NAME_REMINDERS)
data class Reminder(
        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "text")
        var text: String? = null,

        @ColumnInfo(name = "icon_res_name")
        var iconName: String,

        @ColumnInfo(name = "timestamp")
        var timestamp: Long = 0) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
