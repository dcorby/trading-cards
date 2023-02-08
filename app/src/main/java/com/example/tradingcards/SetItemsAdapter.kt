package com.example.tradingcards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// import android.widget.ListAdapter NO!
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class SetItemsAdapter(private val onClick: (SetItem) -> Unit) :
    ListAdapter<SetItem, SetItemsAdapter.SetItemViewHolder>(SetItemDiffCallback) {

    inner class SetItemViewHolder(setItemView: View,
                                   val onClick: (SetItem) -> Unit) : RecyclerView.ViewHolder(setItemView) {

        private val setItemView = setItemView
        private val setItemTextView: TextView = setItemView.findViewById(R.id.set_item_text_view)

        /* Bind data to view */
        fun bind(setItem: SetItem) {
            setItemTextView.text = setItem.playerName

            // CHECK THIS AGAINST FLOWERS IMPLEMENTATIONS
            setItemView.setOnClickListener { onClick(setItem) }
        }
    }

    /* Creates and inflates view and returns SetItemViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.set_item, parent, false)
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
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: SetItem, newItem: SetItem): Boolean {
        return oldItem.itemId == newItem.itemId
    }
}