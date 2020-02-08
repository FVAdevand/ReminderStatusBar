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
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.utils.IconUtils
import ua.fvadevand.reminderstatusbar.utils.getNotificationTime

class ReminderAdapter(
    private val listener: (Long) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var reminders: MutableList<Reminder> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    fun setReminders(newList: List<Reminder>) {
        val diffUtilCallback = ReminderDiffUtilCallback(reminders, newList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        reminders.clear()
        reminders.addAll(newList)
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun isValidPosition(position: Int) = position in 0 until itemCount

    inner class ReminderDiffUtilCallback(
        private val oldList: List<Reminder>,
        private val newList: List<Reminder>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconView: ImageView = itemView.findViewById(R.id.iv_item_reminder_icon)
        private val titleView: TextView = itemView.findViewById(R.id.tv_item_reminder_title)
        private val textView: TextView = itemView.findViewById(R.id.tv_item_reminder_text)
        private val dateView: TextView = itemView.findViewById(R.id.tv_item_reminder_date)
        private val statusView: ImageView = itemView.findViewById(R.id.iv_item_reminder_status)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (isValidPosition(position)) {
                    listener(reminders[position].id)
                }
            }
        }

        fun bind(reminder: Reminder) {
            iconView.setImageResource(IconUtils.toResId(iconView.context, reminder.iconName))
            titleView.text = reminder.title
            val reminderText = reminder.text
            if (reminderText.isNullOrEmpty()) {
                textView.isVisible = false
            } else {
                textView.isVisible = true
                textView.text = reminderText
            }
            if (reminder.timestamp > System.currentTimeMillis()) {
                dateView.isVisible = true
                dateView.text = itemView.context.getNotificationTime(reminder.timestamp)
            } else {
                dateView.isVisible = false
            }
            statusView.setImageResource(ReminderStatus.getIconResIdByStatus(reminder.status))
        }
    }
}
