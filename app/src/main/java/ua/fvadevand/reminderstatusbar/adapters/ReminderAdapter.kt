package ua.fvadevand.reminderstatusbar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.utils.IconUtils

class ReminderAdapter(
        private val listener: (Long) -> Unit
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

    inner class ReminderDiffUtilCallback(
            private val mOldList: List<Reminder>,
            private val mNewList: List<Reminder>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition].id == mNewList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition] == mNewList[newItemPosition]
        }
    }

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mIconView: ImageView = itemView.findViewById(R.id.iv_item_icon)
        private var mTitleView: TextView = itemView.findViewById(R.id.tv_item_title)
        private var mTextView: TextView = itemView.findViewById(R.id.tv_item_text)
        private var mDateView: TextView = itemView.findViewById(R.id.tv_item_date)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position in 0 until itemCount) {
                    listener(reminders[position].id)
                }
            }
        }

        fun bind(reminder: Reminder) {
            val context = itemView.context
            mIconView.setImageResource(IconUtils.getIconResId(context, reminder.iconName))
            mTitleView.text = reminder.title
            mTextView.text = reminder.text
        }
    }
}
