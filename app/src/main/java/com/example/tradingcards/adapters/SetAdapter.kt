package com.example.tradingcards.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
// import android.widget.ListAdapter NO!
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
    var prevName = ""

    inner class SetItemViewHolder(setItemView: View,
                                   val onClick: (SetItem) -> Unit) : RecyclerView.ViewHolder(setItemView) {

        private val setItemView = setItemView
        private val setItemTextView: TextView = setItemView.findViewById(R.id.text_view)

        /* Bind data to view */
        fun bind(setItem: SetItem) {
            setItemTextView.text = setItem.name + " (${setItem.playerName})"
            //setItemView.tag = "Zzz"

            // CHECK THIS AGAINST FLOWERS IMPLEMENTATIONS
            //setItemView.setOnClickListener { onClick(setItem) }

            tracker.let {
                if (it.isSelected(getItem(position).name)) {
                    if (getItem(position).name != prevName) {
                        tracker.deselect(prevName)
                    }
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue1))
                    prevName = getItem(position).name
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
                override fun getSelectionKey(): String = currentList[adapterPosition].name
                override fun inSelectionHotspot(e: MotionEvent): Boolean { return true }
                // this will override any existing itemView.setOnClickListener()
            }
    }

    /* Creates and inflates view and returns SetItemViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return SetItemViewHolder(view, onClick)
    }

    /* Gets current ListItem and uses it to bind view */
    override fun onBindViewHolder(viewHolder: SetItemViewHolder, position: Int) {
        val listItem = getItem(position)
        viewHolder.bind(listItem)
    }
}

object SetItemDiffCallback : DiffUtil.ItemCallback<SetItem>() {
    override fun areItemsTheSame(oldItem: SetItem, newItem: SetItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: SetItem, newItem: SetItem): Boolean {
        return oldItem.name == newItem.name
    }
}