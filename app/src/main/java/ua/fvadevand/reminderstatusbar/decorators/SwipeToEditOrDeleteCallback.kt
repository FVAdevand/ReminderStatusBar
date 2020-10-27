package ua.fvadevand.reminderstatusbar.decorators

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R

class SwipeToEditOrDeleteCallback(context: Context, private val adapter: SwipeableAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val iconEdit: Drawable?
    private val iconDelete: Drawable?
    private val backgroundEdit: ColorDrawable
    private val backgroundDelete: ColorDrawable
    private val backgroundOffset: Int
    private val iconOffset: Int

    init {
        with(context) {
            iconEdit = ContextCompat.getDrawable(this, R.drawable.ic_swipe_edit)?.apply {
                setTint(getColor(R.color.colorSwipeableItemIcon))
            }
            iconDelete = ContextCompat.getDrawable(this, R.drawable.ic_swipe_delete)?.apply {
                setTint(getColor(R.color.colorSwipeableItemIcon))
            }
            backgroundDelete = ColorDrawable(getColor(R.color.colorSwipeableItemDeleteBackground))
            backgroundEdit = ColorDrawable(getColor(R.color.colorSwipeableItemEditBackground))
            backgroundOffset =
                resources.getDimensionPixelSize(R.dimen.item_reminder_background_corner_radius)
            iconOffset =
                resources.getDimensionPixelSize(R.dimen.item_reminder_swipe_icon_margin_edge)
        }
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (adapter.isItemSwipeable(viewHolder.adapterPosition)) {
            super.getSwipeDirs(recyclerView, viewHolder)
        } else {
            0
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.RIGHT) {
            adapter.editItem(viewHolder.adapterPosition)
        } else {
            adapter.deleteItem(viewHolder.adapterPosition)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val background = if (dX > 0) backgroundEdit else backgroundDelete
        drawBackground(c, viewHolder.itemView, dX, background)
        val icon = if (dX > 0) iconEdit else iconDelete
        icon?.let {
            drawIcon(c, viewHolder.itemView, dX, it)
        }
    }

    private fun drawBackground(c: Canvas, view: View, dX: Float, background: ColorDrawable) {
        when {
            dX > 0 -> { // Swiping to the right
                background.setBounds(
                    view.left, view.top,
                    view.left + dX.toInt() + backgroundOffset,
                    view.bottom
                )
            }
            dX < 0 -> { // Swiping to the left
                background.setBounds(
                    view.right + dX.toInt() - backgroundOffset,
                    view.top, view.right, view.bottom
                )
            }
            else -> { // view is unSwiped
                background.setBounds(0, 0, 0, 0)
            }
        }
        background.draw(c)
    }

    private fun drawIcon(c: Canvas, view: View, dX: Float, icon: Drawable) {
        val iconMargin: Int = (view.height - icon.intrinsicHeight) / 2
        val iconTop: Int = view.top + iconMargin
        val iconBottom = iconTop + icon.intrinsicHeight

        when {
            dX > 0 -> { // Swiping to the right
                val iconLeft: Int = view.left + iconOffset
                val iconRight: Int = iconLeft + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
            dX < 0 -> { // Swiping to the left
                val iconRight: Int = view.right - iconOffset
                val iconLeft: Int = iconRight - icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
        }
        icon.draw(c)
    }

    interface SwipeableAdapter {
        fun editItem(position: Int)
        fun deleteItem(position: Int)
        fun isItemSwipeable(position: Int): Boolean
    }
}