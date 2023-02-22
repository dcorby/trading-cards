package com.example.tradingcards.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.items.SetItem

class SetAdapter(private val onClick: (SetItem) -> Unit) :
    ListAdapter<SetItem, SetAdapter.SetItemViewHolder>(SetItemDiffCallback) {

    lateinit var tracker: SelectionTracker<String>
    var prevFilename = ""

    inner class SetItemViewHolder(
            private val itemView: View,
            val onClick: (SetItem) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.text_view)
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        // Bind data to view
        fun bind(setItem: SetItem) {
            textView.text = setItem.label

            imageView.setImageResource(
                if (setItem.isCard) {
                    R.drawable.ic_baseline_image_32_green
                } else {
                    R.drawable.ic_baseline_folder_32
                })

            tracker.let {
                if (it.isSelected(getItem(position).filename)) {
                    if (getItem(position).filename != prevFilename) {
                        tracker.deselect(prevFilename)
                    }
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue1))
                    prevFilename = getItem(position).filename
                } else {
                    itemView.setBackgroundColor(Color.parseColor("#ffffff"))
                }
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }
                override fun getSelectionKey(): String = currentList[adapterPosition].filename
                override fun inSelectionHotspot(e: MotionEvent): Boolean { return true }
                // this will override an existing itemView.setOnClickListener()
            }
    }

    // Creates and inflates view and returns SetItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return SetItemViewHolder(view, onClick)
    }

    // Gets current ListItem and uses it to bind view
    override fun onBindViewHolder(viewHolder: SetItemViewHolder, position: Int) {
        val listItem = getItem(position)
        viewHolder.bind(listItem)
    }
}

object SetItemDiffCallback : DiffUtil.ItemCallback<SetItem>() {
    override fun areItemsTheSame(oldItem: SetItem, newItem: SetItem): Boolean {
        return oldItem.filename == newItem.filename
    }

    override fun areContentsTheSame(oldItem: SetItem, newItem: SetItem): Boolean {
        return oldItem.filename == newItem.filename
    }
}