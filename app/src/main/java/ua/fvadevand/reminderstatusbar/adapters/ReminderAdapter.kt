package ua.fvadevand.reminderstatusbar.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.listeners.OnReminderClickListener
import ua.fvadevand.reminderstatusbar.utils.ReminderDateUtils
import java.util.Calendar

class ReminderAdapter(
        private val context: Context,
        private val listener: OnReminderClickListener
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var reminders: MutableList<Reminder> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_list_item, parent, false)
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

        private val iconView: ImageView = itemView.findViewById(R.id.iv_item_icon)
        private val titleView: TextView = itemView.findViewById(R.id.tv_item_title)
        private val textView: TextView = itemView.findViewById(R.id.tv_item_text)
        private val dateView: TextView = itemView.findViewById(R.id.tv_item_date)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.ibtn_item_action_delete)
        private val notifyBtn: ImageButton = itemView.findViewById(R.id.ibtn_item_action_notify)
        private val itemViewId: Int = itemView.id
        private val onClickListener = View.OnClickListener {
            val position = adapterPosition
            if (!isValidPosition(position)) return@OnClickListener
            val reminderId = reminders[position].id
            when (it.id) {
                itemViewId -> listener.onClickReminder(reminderId)
                R.id.ibtn_item_action_delete -> listener.onClickReminderDelete(reminderId)
                R.id.ibtn_item_action_notify -> listener.onClickReminderNotify(reminderId)
            }
        }

        init {
            itemView.setOnClickListener(onClickListener)
            deleteBtn.setOnClickListener(onClickListener)
            notifyBtn.setOnClickListener(onClickListener)
        }

        fun bind(reminder: Reminder) {
            iconView.setImageResource(reminder.iconResId)
            titleView.text = reminder.title
            val reminderText = reminder.text
            if (TextUtils.isEmpty(reminderText)) {
                textView.visibility = View.GONE
            } else {
                textView.visibility = View.VISIBLE
                textView.text = reminderText
            }
            if (reminder.timestamp > System.currentTimeMillis()) {
                dateView.visibility = View.VISIBLE
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = reminder.timestamp
                dateView.text = ReminderDateUtils.getNotificationTime(context, calendar)
            } else {
                dateView.visibility = View.INVISIBLE
            }
        }
    }
}
