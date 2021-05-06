package ua.fvadevand.reminderstatusbar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.adapters.utils.ReminderDiffUtil
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.databinding.ItemReminderBinding
import ua.fvadevand.reminderstatusbar.decorators.SwipeToEditOrDeleteCallback
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener
import ua.fvadevand.reminderstatusbar.utils.getNotificationTime
import ua.fvadevand.reminderstatusbar.utils.setImageResourceName

class ReminderAdapter(
    private val listener: OnReminderInteractListener?
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>(),
    SwipeToEditOrDeleteCallback.SwipeableAdapter {

    private val differ = AsyncListDiffer(this, ReminderDiffUtil())
    private val reminders get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val itemBinding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_reminder
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    override fun allowSwipe(position: Int): Boolean {
        return true
    }

    override fun editItem(position: Int) {
        if (isValidPosition(position)) {
            notifyItemChanged(position)
            listener?.onReminderEdit(getReminderByPosition(position).id)
        }
    }

    override fun deleteItem(position: Int) {
        if (isValidPosition(position)) {
            listener?.onReminderDelete(getReminderByPosition(position))
        }
    }

    fun submitReminders(newList: List<Reminder>) {
        differ.submitList(newList)
    }

    private fun isValidPosition(position: Int) = position in 0 until itemCount

    private fun getReminderByPosition(position: Int) = reminders[position]

    inner class ReminderViewHolder(
        private val itemBinding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (isValidPosition(position)) {
                    listener?.onReminderClick(getReminderByPosition(position).id)
                }
            }
        }

        fun bind(reminder: Reminder) {
            itemBinding.ivItemReminderIcon.setImageResourceName(reminder.iconName)
            itemBinding.tvItemReminderTitle.text = reminder.title
            val reminderText = reminder.text
            if (reminderText.isNullOrEmpty()) {
                itemBinding.tvItemReminderText.isVisible = false
            } else {
                itemBinding.tvItemReminderText.isVisible = true
                itemBinding.tvItemReminderText.text = reminderText
            }
            if (reminder.timestamp > System.currentTimeMillis() &&
                reminder.status != ReminderStatus.PAUSED
            ) {
                itemBinding.tvItemReminderDate.isVisible = true
                itemBinding.tvItemReminderDate.text =
                    itemView.context.getNotificationTime(reminder.timestamp)
            } else {
                itemBinding.tvItemReminderDate.isVisible = false
            }
            itemBinding.ivItemReminderStatus.setImageResource(
                ReminderStatus.getIconResIdByStatus(reminder.status)
            )
        }
    }

}
