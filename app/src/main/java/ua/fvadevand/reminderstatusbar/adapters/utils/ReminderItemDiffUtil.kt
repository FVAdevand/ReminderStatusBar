package ua.fvadevand.reminderstatusbar.adapters.utils

import androidx.recyclerview.widget.DiffUtil
import ua.fvadevand.reminderstatusbar.data.models.ReminderItem

class ReminderItemDiffUtil : DiffUtil.ItemCallback<ReminderItem>() {

    override fun areItemsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
        if (oldItem.type != newItem.type) return false
        if (oldItem.type == ReminderItem.TYPE_HEADER) return true
        return (oldItem as ReminderItem.Data).reminder.id == (newItem as ReminderItem.Data).reminder.id
    }

    override fun areContentsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
        if (oldItem.type != newItem.type) return false
        if (oldItem.type == ReminderItem.TYPE_HEADER) return true
        return (oldItem as ReminderItem.Data).reminder == (newItem as ReminderItem.Data).reminder
    }
}