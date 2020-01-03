package se.hkr.smarthouse.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopSpacingItemDecoration(
    private val topPadding: Int = 0,
    private val bottomPadding: Int = 0,
    private val leftPadding: Int = 0,
    private val rightPadding: Int = 0
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = topPadding
        outRect.bottom = bottomPadding
        outRect.left = leftPadding
        outRect.right = rightPadding
    }
}