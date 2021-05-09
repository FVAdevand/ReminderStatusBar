package ua.fvadevand.reminderstatusbar.adapters

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
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
    private val highlightColor: Int,
    private val listener: OnReminderInteractListener?
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>(),
    SwipeToEditOrDeleteCallback.SwipeableAdapter {

    private val differ = AsyncListDiffer(this, ReminderDiffUtil())
    private val reminders get() = differ.currentList
    private var highlightPosition = -1
    private val rgbEvaluator by lazy { ArgbEvaluator() }

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
        if (position == highlightPosition) {
            highlightPosition = -1
            holder.highlight()
        }
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

    fun submitReminders(newList: List<Reminder>, onComplete: () -> Unit) {
        differ.submitList(newList, onComplete)
    }

    fun getReminderIndex(reminderId: Long): Int {
        return reminders.indexOfFirst { it.id == reminderId }
    }

    fun highlightItem(position: Int) {
        highlightPosition = position
        notifyItemChanged(position)
    }

    private fun isValidPosition(position: Int) = position in 0 until itemCount

    private fun getReminderByPosition(position: Int) = reminders[position]

    inner class ReminderViewHolder(
        private val itemBinding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private var animator: AnimatorSet? = null

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

        fun highlight() {
            animator?.end()
            val backgroundDrawable = itemView.background as? GradientDrawable ?: return
            val backgroundColor = backgroundDrawable.color?.defaultColor ?: return
            backgroundDrawable.mutate()
            val inAnimation = ValueAnimator.ofFloat(0F, 1F).apply {
                duration = 750
                addUpdateListener {
                    backgroundDrawable.setColor(
                        rgbEvaluator.evaluate(
                            it.animatedFraction,
                            backgroundColor,
                            highlightColor
                        ) as Int
                    )
                }
            }

            val outAnimation = ValueAnimator.ofFloat(0F, 1F).apply {
                duration = 750
                addUpdateListener {
                    backgroundDrawable.setColor(
                        rgbEvaluator.evaluate(
                            it.animatedFraction,
                            highlightColor,
                            backgroundColor
                        ) as Int
                    )
                }
            }

            animator = AnimatorSet().apply {
                playSequentially(inAnimation, outAnimation)
                doOnStart { itemView.setHasTransientState(true) }
                doOnEnd {
                    animator = null
                    itemView.setHasTransientState(false)
                }
                start()
            }
        }

    }

}
