package me.hanhngo.qrcode.view.history

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import me.hanhngo.qrcode.adapter.BarcodeItemAdapter
import me.hanhngo.qrcode.adapter.ItemClickListener
import me.hanhngo.qrcode.domain.BarcodeItem

@BindingAdapter("items", "listener")
fun bindItem(
    recyclerView: RecyclerView,
    items: List<BarcodeItem>?,
    listener: ItemClickListener
) {
    val adapter = getOrCreateAdapter(recyclerView, listener)
    val list = items?.sortedByDescending { it.dateTime }
    adapter.submitList(list)
    recyclerView.smoothScrollToPosition(0)
}

private fun getOrCreateAdapter(
    recyclerView: RecyclerView,
    listener: ItemClickListener
): BarcodeItemAdapter {
    return if (recyclerView.adapter != null && recyclerView.adapter is BarcodeItemAdapter) {
        recyclerView.adapter as BarcodeItemAdapter
    } else {
        val listBillAdapter = BarcodeItemAdapter(listener)
        recyclerView.adapter = listBillAdapter
        listBillAdapter
    }
}