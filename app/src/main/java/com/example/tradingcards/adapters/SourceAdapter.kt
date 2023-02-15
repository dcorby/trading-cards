package com.example.tradingcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
// import android.widget.ListAdapter NO!
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.items.SourceItem

class SourceAdapter(private val onClick: (SourceItem) -> Unit) :
    ListAdapter<SourceItem, SourceAdapter.SourceItemViewHolder>(SourceItemDiffCallback) {

    inner class SourceItemViewHolder(sourceItemView: View,
                                  val onClick: (SourceItem) -> Unit) : RecyclerView.ViewHolder(sourceItemView) {

        private val sourceItemView = sourceItemView
        private val sourceItemTextView: TextView = sourceItemView.findViewById(R.id.textview)
        private val sourceItemCheckBox: CheckBox = sourceItemView.findViewById(R.id.checkbox)

        /* Bind data to view */
        fun bind(sourceItem: SourceItem) {
            sourceItemTextView.text = sourceItem.label
            sourceItemCheckBox.isChecked = sourceItem.synced

            // CHECK THIS AGAINST FLOWERS IMPLEMENTATIONS
            //sourceItemView.setOnClickListener { onClick(sourceItem) }
            // This will get called over and over by the recycler view
            //sourceItemCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->  onCheck()
            sourceItemCheckBox.setOnClickListener{ onClick(sourceItem) }
        }
    }

    /* Creates and inflates view and returns SetItemViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_source, parent, false)
        return SourceItemViewHolder(view, onClick)
    }

    /* Gets current ListItem and uses it to bind view */
    override fun onBindViewHolder(viewHolder: SourceItemViewHolder, position: Int) {
        val listItem = getItem(position)
        viewHolder.bind(listItem)
    }
}

object SourceItemDiffCallback : DiffUtil.ItemCallback<SourceItem>() {
    override fun areItemsTheSame(oldItem: SourceItem, newItem: SourceItem): Boolean {
        return oldItem.label == newItem.label
    }

    override fun areContentsTheSame(oldItem: SourceItem, newItem: SourceItem): Boolean {
        return oldItem.label == newItem.label
    }
}