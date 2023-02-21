package com.example.tradingcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.items.PlayerItem

class PlayerAdapter(private val onClick: (PlayerItem) -> Unit) :
    ListAdapter<PlayerItem, PlayerAdapter.PlayerItemViewHolder>(PlayerItemDiffCallback) {

    inner class PlayerItemViewHolder(playerItemView: View,
                                  val onClick: (PlayerItem) -> Unit) : RecyclerView.ViewHolder(playerItemView) {

        private val playerItemView = playerItemView
        private val playerItemTextView: TextView = playerItemView.findViewById(R.id.text_view)

        /* Bind data to view */
        fun bind(playerItem: PlayerItem) {

            playerItemTextView.text = playerItem.name + " (${playerItem.id})"

            // Make sure there's no active selection tracker. It really messes with this.
            playerItemView.setOnClickListener {
                onClick(playerItem)
            }
        }
    }

    /* Creates and inflates view and returns SetItemViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return PlayerItemViewHolder(view, onClick)
    }

    /* Gets current ListItem and uses it to bind view */
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