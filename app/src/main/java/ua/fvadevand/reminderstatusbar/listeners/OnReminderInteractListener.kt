package ua.fvadevand.reminderstatusbar.listeners

import ua.fvadevand.reminderstatusbar.data.models.Reminder

interface OnReminderInteractListener {
    fun onReminderClick(id: Long)

    fun onReminderEdit(id: Long)

    fun onReminderDelete(reminder: Reminder)
}