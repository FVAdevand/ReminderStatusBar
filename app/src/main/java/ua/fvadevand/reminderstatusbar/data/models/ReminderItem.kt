package ua.fvadevand.reminderstatusbar.data.models

sealed class ReminderItem {

    abstract val type: Int

    data class Header(val text: String) : ReminderItem() {
        override val type = TYPE_HEADER
    }

    data class Data(val reminder: Reminder) : ReminderItem() {
        override val type = TYPE_REMINDER
    }

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_REMINDER = 2
    }

}