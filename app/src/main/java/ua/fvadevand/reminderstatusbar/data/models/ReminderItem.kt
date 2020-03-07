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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReminderItem

        if (type != other.type) return false
        if (header != other.header) return false
        if (reminder != other.reminder) return false

        return true
    }

    override fun hashCode(): Int {
        return 31 * type + if (type == TYPE_HEADER) header.hashCode() else reminder.hashCode()
    }


}