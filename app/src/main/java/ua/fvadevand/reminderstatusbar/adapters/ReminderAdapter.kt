package ua.fvadevand.reminderstatusbar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.adapters.utils.ReminderItemDiffUtil
import ua.fvadevand.reminderstatusbar.data.models.ReminderItem
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.databinding.ListItemReminderBinding
import ua.fvadevand.reminderstatusbar.databinding.ListItemReminderHeaderBinding
import ua.fvadevand.reminderstatusbar.decorators.SwipeToEditOrDeleteCallback
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener
import ua.fvadevand.reminderstatusbar.utils.getNotificationTime
import ua.fvadevand.reminderstatusbar.utils.setImageResourceName

class ReminderAdapter(
    private val listener: OnReminderInteractListener?
) : RecyclerView.Adapter<ReminderAdapter.BaseReminderViewHolder>(),
    SwipeToEditOrDeleteCallback.SwipeableAdapter {

    private val differ = AsyncListDiffer(this, ReminderItemDiffUtil())
    private val reminders get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseReminderViewHolder {
        return when (viewType) {
            ReminderItem.TYPE_HEADER -> {
                val itemBinding = ListItemReminderHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(itemBinding)
            }

            ReminderItem.TYPE_REMINDER -> {
                val itemBinding = ListItemReminderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReminderViewHolder(itemBinding)
            }

            else -> throw IllegalArgumentException("Invalid item type $viewType, type must be one from ReminderItem.TYPE")
        }
    }

    override fun onBindViewHolder(holder: BaseReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemViewType(position: Int): Int {
        return reminders[position].type
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    override fun isItemSwipeable(position: Int): Boolean {
        return getItemViewType(position) != ReminderItem.TYPE_HEADER
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

    fun submitReminders(newList: List<ReminderItem>) {
        differ.submitList(newList)
    }

    private fun isValidPosition(position: Int) = position in 0 until itemCount

    private fun getReminderByPosition(position: Int) =
        (reminders[position] as ReminderItem.Data).reminder

    abstract class BaseReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(reminderItem: ReminderItem)
    }

    class HeaderViewHolder(
        private val itemBinding: ListItemReminderHeaderBinding
    ) : BaseReminderViewHolder(itemBinding.root) {
        override fun bind(reminderItem: ReminderItem) {
            reminderItem as ReminderItem.Header
            itemBinding.tvRemindersHeader.text = reminderItem.text
        }
    }

    inner class ReminderViewHolder(
        private val itemBinding: ListItemReminderBinding
    ) : BaseReminderViewHolder(itemBinding.root) {
        init {
            itemBinding.root.setOnClickListener {
                val position = adapterPosition
                if (isValidPosition(position)) {
                    listener?.onReminderClick(getReminderByPosition(position).id)
                }
            }
        }

        override fun bind(reminderItem: ReminderItem) {
            reminderItem as ReminderItem.Data
            itemBinding.ivItemReminderIcon.setImageResourceName(reminderItem.reminder.iconName)
            itemBinding.tvItemReminderTitle.text = reminderItem.reminder.title
            val reminderText = reminderItem.reminder.text
            if (reminderText.isNullOrEmpty()) {
                itemBinding.tvItemReminderText.isVisible = false
            } else {
                itemBinding.tvItemReminderText.isVisible = true
                itemBinding.tvItemReminderText.text = reminderText
            }
            if (reminderItem.reminder.timestamp > System.currentTimeMillis() &&
                reminderItem.reminder.status != ReminderStatus.PAUSED
            ) {
                itemBinding.tvItemReminderDate.isVisible = true
                itemBinding.tvItemReminderDate.text =
                    itemView.context.getNotificationTime(reminderItem.reminder.timestamp)
            } else {
                itemBinding.tvItemReminderDate.isVisible = false
            }
            itemBinding.ivItemReminderStatus.setImageResource(
                ReminderStatus.getIconResIdByStatus(reminderItem.reminder.status)
            )
        }
    }
}
