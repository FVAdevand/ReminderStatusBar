package ua.fvadevand.reminderstatusbar.data.models

import androidx.room.Embedded
import androidx.room.Ignore

class ReminderItem(@Ignore val type: Int = TYPE_REMINDER) {

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_REMINDER = 2
    }

    @Ignore
    lateinit var header: String
    @Embedded
    lateinit var reminder: Reminder

    override fun toString() = when (type) {
        TYPE_HEADER -> "ReminderItem(type=$type, header='$header')"
        TYPE_REMINDER -> "ReminderItem(type=$type, reminder=$reminder)"
        else -> "ReminderItem(Unknown type=$type)"
    }

}