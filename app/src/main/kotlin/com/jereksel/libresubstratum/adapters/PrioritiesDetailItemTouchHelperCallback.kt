package com.jereksel.libresubstratum.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.DOWN
import android.support.v7.widget.helper.ItemTouchHelper.UP

class PrioritiesDetailItemTouchHelperCallback(
        val adapter: PrioritiesDetailAdapter
): ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = UP or DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) = Unit

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = false

}