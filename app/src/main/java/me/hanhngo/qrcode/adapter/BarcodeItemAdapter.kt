package me.hanhngo.qrcode.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.hanhngo.qrcode.databinding.ItemBarcodeRowBinding
import me.hanhngo.qrcode.domain.BarcodeItem
import me.hanhngo.qrcode.util.extension.toFormattedString

class BarcodeItemAdapter(
    private val listener: ItemClickListener
) :
    ListAdapter<BarcodeItem, BarcodeItemAdapter.BarcodeItemViewHolder>(DIFF_CALL_BACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeItemViewHolder {
        val binding = ItemBarcodeRowBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return BarcodeItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarcodeItemViewHolder, position: Int) {
        getItem(position)?.also {
            holder.bind(it, listener)
        }
    }

    class BarcodeItemViewHolder(
        private val binding: ItemBarcodeRowBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(barcodeItem: BarcodeItem, listener: ItemClickListener) {
            binding.iconIv.setImageResource(barcodeItem.resId)
            binding.dateTv.text = barcodeItem.dateTime.toFormattedString()
            binding.setVariable(BR.listener, listener)
            binding.barcodeItem = barcodeItem
            binding.executePendingBindings()
        }
    }

    companion object {
        val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<BarcodeItem>() {
            override fun areItemsTheSame(oldItem: BarcodeItem, newItem: BarcodeItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BarcodeItem, newItem: BarcodeItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}

class ItemClickListener(val clickListener: (barcodeItem: BarcodeItem) -> Unit) {
    fun onItemClickListener(barcodeItem: BarcodeItem) = clickListener(barcodeItem)
}