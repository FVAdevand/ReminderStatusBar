package ua.fvadevand.reminderstatusbar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.ReminderItem
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.decorators.SwipeToEditOrDeleteCallback
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener
import ua.fvadevand.reminderstatusbar.utils.getNotificationTime
import ua.fvadevand.reminderstatusbar.utils.setImageResourceName

class ReminderAdapter(
    private val listener: OnReminderInteractListener?
) : RecyclerView.Adapter<ReminderAdapter.BaseReminderViewHolder>(),
    SwipeToEditOrDeleteCallback.SwipeableAdapter {

    private var reminders: MutableList<ReminderItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseReminderViewHolder {
        return when (viewType) {
            ReminderItem.TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_reminder_header, parent, false)
                HeaderViewHolder(view)
            }

            ReminderItem.TYPE_REMINDER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_reminder, parent, false)
                ReminderViewHolder(view)
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
            listener?.onReminderEdit(reminders[position].reminder.id)
        }
    }

    override fun deleteItem(position: Int) {
        if (isValidPosition(position)) {
            listener?.onReminderDelete(reminders[position].reminder)
        }
    }

    fun setReminders(newList: List<ReminderItem>) {
        val diffUtilCallback = ReminderDiffUtilCallback(reminders, newList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        reminders.clear()
        reminders.addAll(newList)
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun isValidPosition(position: Int) = position in 0 until itemCount

    inner class ReminderDiffUtilCallback(
        private val oldList: List<ReminderItem>,
        private val newList: List<ReminderItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (oldList[oldItemPosition].type != newList[newItemPosition].type) return false
            if (oldList[oldItemPosition].type == ReminderItem.TYPE_HEADER) return true
            return oldList[oldItemPosition].reminder.id == newList[newItemPosition].reminder.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (oldList[oldItemPosition].type != newList[newItemPosition].type) return false
            if (oldList[oldItemPosition].type == ReminderItem.TYPE_HEADER) return true
            return oldList[oldItemPosition].reminder == newList[newItemPosition].reminder
        }
    }

    abstract class BaseReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(reminderItem: ReminderItem)
    }

    class HeaderViewHolder(itemView: View) : BaseReminderViewHolder(itemView) {
        private val headerView: TextView = itemView.findViewById(R.id.tv_reminders_header)
        override fun bind(reminderItem: ReminderItem) {
            headerView.text = reminderItem.header
        }
    }

    inner class ReminderViewHolder(itemView: View) : BaseReminderViewHolder(itemView) {
        private val iconView: ImageView = itemView.findViewById(R.id.iv_item_reminder_icon)
        private val titleView: TextView = itemView.findViewById(R.id.tv_item_reminder_title)
        private val textView: TextView = itemView.findViewById(R.id.tv_item_reminder_text)
        private val dateView: TextView = itemView.findViewById(R.id.tv_item_reminder_date)
        private val statusView: ImageView = itemView.findViewById(R.id.iv_item_reminder_status)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (isValidPosition(position)) {
                    listener?.onReminderClick(reminders[position].reminder.id)
                }
            }
        }

        override fun bind(reminderItem: ReminderItem) {
            iconView.setImageResourceName(reminderItem.reminder.iconName)
            titleView.text = reminderItem.reminder.title
            val reminderText = reminderItem.reminder.text
            if (reminderText.isNullOrEmpty()) {
                textView.isVisible = false
            } else {
                textView.isVisible = true
                textView.text = reminderText
            }
            if (reminderItem.reminder.timestamp > System.currentTimeMillis()) {
                dateView.isVisible = true
                dateView.text =
                    itemView.context.getNotificationTime(reminderItem.reminder.timestamp)
            } else {
                dateView.isVisible = false
            }
            statusView.setImageResource(ReminderStatus.getIconResIdByStatus(reminderItem.reminder.status))
        }
    }
}
