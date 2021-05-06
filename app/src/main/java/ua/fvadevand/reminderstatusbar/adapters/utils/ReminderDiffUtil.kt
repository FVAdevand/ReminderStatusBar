package ua.fvadevand.reminderstatusbar.adapters.utils

import androidx.recyclerview.widget.DiffUtil
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class ReminderDiffUtil : DiffUtil.ItemCallback<Reminder>() {

    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }

}