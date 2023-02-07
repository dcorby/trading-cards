package com.example.tradingcards

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ListAdapter
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class CardItemsAdapter(private val onClick: (CardItem) -> Unit) :
    ListAdapter<CardItem, CardItemsAdapter.CardItemViewHolder>(CardItemDiffCallback) {

    inner class CardItemViewHolder(cardItemView: View,
                                   val onClick: (CardItem) -> Unit) : RecyclerView.ViewHolder(cardItemView) {

        private val cardItemTextView: TextView = cardItemView.findViewById(R.id.card_item_text_view)

        /* Bind data to view */
        fun bind(cardItem: CardItem) {
            cardItemTextView.text = cardItem.playerName
        }
    }
}

object CardItemDiffCallback : DiffUtil.ItemCallback<CardItem>() {
    override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
        return oldItem.cardId == newItem.cardId
    }

    override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
        return oldItem.cardId == newItem.cardId
    }
}