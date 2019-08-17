package ua.fvadevand.reminderstatusbar.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OutsideOffsetItemDecorator(
        private val offsetTop: Int,
        private val offsetBottom: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemCount = state.itemCount
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return
        if (position == 0) {
            outRect.top = offsetTop
        }
        if (position == itemCount - 1) {
            outRect.bottom = offsetBottom
        }
    }
}