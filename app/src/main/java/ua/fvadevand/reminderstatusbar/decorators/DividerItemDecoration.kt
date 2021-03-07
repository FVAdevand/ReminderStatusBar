package ua.fvadevand.reminderstatusbar.decorators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.utils.logW
import kotlin.math.roundToInt

/**
 * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
 * [LinearLayoutManager].
 *
 * @param context          Current context, it will be used to access resources.
 * @param orientation      Divider orientation. Should be [LinearLayout.HORIZONTAL] or [LinearLayout.VERTICAL].
 * @param offsetStart      Offset divider left in LinearLayout.VERTICAL or top in LinearLayout.HORIZONTAL
 * @param offsetEnd        Offset divider right in LinearLayout.VERTICAL or bottom in LinearLayout.HORIZONTAL
 * @param drawInFirstItem  Whether show the divider in first item.
 * @param drawInLastItem   Whether show the divider in last item.
 */

class DividerItemDecoration(
    private val context: Context,
    /**
     * Current orientation. Either [LinearLayout.HORIZONTAL] or [LinearLayout.VERTICAL].
     */
    private val orientation: Int,
    private val offsetStart: Int,
    private val offsetEnd: Int,
    private val drawInFirstItem: Boolean = true,
    private val drawInLastItem: Boolean = true
) : RecyclerView.ItemDecoration() {

    var divider: Drawable? = null

    init {
        val attr = context.obtainStyledAttributes(ATTRS)
        try {
            divider = attr.getDrawable(0)
            if (divider == null) {
                logW(
                    "@android:attr/listDivider was not set in the theme used for this " +
                            "DividerItemDecoration. Please set that attribute all call setDrawable()"
                )
            }
        } finally {
            attr.recycle()
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || divider == null) return
        if (orientation == LinearLayout.VERTICAL) {
            drawVertical(c, parent, state)
        } else {
            drawHorizontal(c, parent, state)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        divider?.let { divider ->
            canvas.save()
            val left: Int
            val right: Int
            if (parent.clipToPadding) {
                left = parent.paddingLeft + offsetStart
                right = parent.width - parent.paddingRight - offsetEnd
                canvas.clipRect(
                    left,
                    parent.paddingTop,
                    right,
                    parent.height - parent.paddingBottom
                )
            } else {
                left = offsetStart
                right = parent.width - offsetEnd
            }
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val position = parent.getChildAdapterPosition(child)
                if ((drawInFirstItem || position > 0) &&
                    (drawInLastItem || position < state.itemCount - 1)
                ) {
                    val decoratedBottom = parent.layoutManager?.getDecoratedBottom(child) ?: 0
                    val bottom = decoratedBottom + child.translationY.roundToInt()
                    val top = bottom - divider.intrinsicHeight
                    divider.setBounds(left, top, right, bottom)
                    divider.draw(canvas)
                }
            }
            canvas.restore()
        }
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        divider?.let { divider ->
            canvas.save()
            val top: Int
            val bottom: Int
            if (parent.clipToPadding) {
                top = parent.paddingTop + offsetStart
                bottom = parent.height - parent.paddingBottom - offsetEnd
                canvas.clipRect(
                    parent.paddingLeft,
                    top,
                    parent.width - parent.paddingRight,
                    bottom
                )
            } else {
                top = 0 + offsetStart
                bottom = parent.height - offsetEnd
            }

            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val position = parent.getChildAdapterPosition(child)
                if ((drawInFirstItem || position > 0) &&
                    (drawInLastItem || position < state.itemCount - 1)
                ) {
                    val decoratedRight = parent.layoutManager?.getDecoratedRight(child) ?: 0
                    val right = decoratedRight + child.translationX.roundToInt()
                    val left = right - divider.intrinsicWidth
                    divider.setBounds(left, top, right, bottom)
                    divider.draw(canvas)
                }
            }
            canvas.restore()
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        divider?.run {
            if (orientation == LinearLayout.VERTICAL) {
                outRect.set(0, 0, 0, intrinsicHeight)
            } else {
                outRect.set(0, 0, intrinsicWidth, 0)
            }
        } ?: outRect.setEmpty()
    }

}