package com.example.tradingcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.items.PlayerItem

class PlayerAdapter(private val onClick: (PlayerItem) -> Unit) :
    ListAdapter<PlayerItem, PlayerAdapter.PlayerItemViewHolder>(PlayerItemDiffCallback) {

    inner class PlayerItemViewHolder(
            private val itemView: View,
            val onClick: (PlayerItem) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.text_view)
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        // Bind data to view
        fun bind(playerItem: PlayerItem) {
            textView.text = playerItem.name
            if (playerItem.hasImage) {
                val greenIcon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_baseline_image_32_green)
                imageView.setImageDrawable(greenIcon)
            }

            // An active selection tracker really messes with this
            itemView.setOnClickListener {
                onClick(playerItem)
            }
        }
    }

    // Creates and inflates view and returns SetItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return PlayerItemViewHolder(view, onClick)
    }

    // Gets current ListItem and uses it to bind view
    override fun onBindViewHolder(viewHolder: PlayerItemViewHolder, position: Int) {
        val listItem = getItem(position)
        viewHolder.bind(listItem)
    }
}

object PlayerItemDiffCallback : DiffUtil.ItemCallback<PlayerItem>() {
    override fun areItemsTheSame(oldItem: PlayerItem, newItem: PlayerItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlayerItem, newItem: PlayerItem): Boolean {
        return oldItem.id == newItem.id
    }
}