package ua.fvadevand.reminderstatusbar.decorators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
 * [LinearLayoutManager].
 *
 * @param context          Current context, it will be used to access resources.
 * @param orientation      Divider orientation. Should be [.HORIZONTAL] or [.VERTICAL].
 * @param shownInLastItem Whether show the divider in last item.
 * @param offsetStart offset divider left in LinearLayout.VERTICAL or top in LinearLayout.HORIZONTAL
 * @param offsetEnd offset divider right in LinearLayout.VERTICAL or bottom in LinearLayout.HORIZONTAL
 */

class DividerItemDecoration(
        private val context: Context,
        /**
         * Current orientation. Either [LinearLayout.HORIZONTAL] or [LinearLayout.VERTICAL].
         */
        private val orientation: Int,
        private val shownInLastItem: Boolean,
        private val offsetStart: Int,
        private val offsetEnd: Int
) : RecyclerView.ItemDecoration() {

    var divider: Drawable? = null

    init {
        val attr = context.obtainStyledAttributes(ATTRS)
        divider = attr.getDrawable(0)
        if (divider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this " + "DividerItemDecoration. Please set that attribute all call setDrawable()")
        }
        attr.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || divider == null) return
        if (orientation == LinearLayout.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft + offsetStart
            right = parent.width - parent.paddingRight - offsetEnd
            canvas.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)
        } else {
            left = offsetStart
            right = parent.width - offsetEnd
        }
        val childCount = if (shownInLastItem) {
            parent.childCount
        } else {
            parent.childCount - 1
        }
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val decoratedBottom = parent.layoutManager!!.getDecoratedBottom(child)
            val bottom = decoratedBottom + child.translationY.roundToInt()
            val top = bottom - divider!!.intrinsicHeight
            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop + offsetStart
            bottom = parent.height - parent.paddingBottom - offsetEnd
            canvas.clipRect(parent.paddingLeft, top,
                    parent.width - parent.paddingRight, bottom)
        } else {
            top = 0 + offsetStart
            bottom = parent.height - offsetEnd
        }

        val childCount = if (shownInLastItem) {
            parent.childCount
        } else {
            parent.childCount - 1
        }
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val decoratedRight = parent.layoutManager!!.getDecoratedRight(child)
            val right = decoratedRight + child.translationX.roundToInt()
            val left = right - divider!!.intrinsicWidth
            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (divider == null) {
            outRect.setEmpty()
            return
        }

        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val itemCount = state.itemCount
        if (!shownInLastItem && itemPosition == itemCount - 1) {
            outRect.setEmpty()
        }
        if (orientation == LinearLayout.VERTICAL) {
            outRect.set(0, 0, 0, divider!!.intrinsicHeight)
            Log.i(TAG, "getItemOffsets: ${divider!!.intrinsicHeight}")
        } else {
            outRect.set(0, 0, divider!!.intrinsicWidth, 0)
        }
    }

    companion object {
        private const val TAG = "DividerItem"
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}