package ua.fvadevand.reminderstatusbar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.databinding.ItemReminderHeaderBinding

class ReminderHeaderAdapter(
    @StringRes private val title: Int
) : RecyclerView.Adapter<ReminderHeaderAdapter.HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val itemBinding = ItemReminderHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeaderViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(title)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_reminder_header
    }

    override fun getItemCount(): Int {
        return 1
    }

    class HeaderViewHolder(
        private val itemBinding: ItemReminderHeaderBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(@StringRes title: Int) {
            itemBinding.tvRemindersHeader.setText(title)
        }
    }

}