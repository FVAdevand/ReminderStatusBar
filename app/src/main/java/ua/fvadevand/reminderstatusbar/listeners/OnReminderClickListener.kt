package ua.fvadevand.reminderstatusbar.listeners

interface OnReminderClickListener {
    fun onClickReminder(id: Long)

    fun onClickReminderDelete(id: Long)

    fun onClickReminderNotify(id: Long)
}