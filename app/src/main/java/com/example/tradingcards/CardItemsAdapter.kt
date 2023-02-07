package com.example.tradingcards

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// import android.widget.ListAdapter NO!
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class CardItemsAdapter(private val onClick: (CardItem) -> Unit) :
    ListAdapter<CardItem, CardItemsAdapter.CardItemViewHolder>(CardItemDiffCallback) {

    inner class CardItemViewHolder(cardItemView: View,
                                   val onClick: (CardItem) -> Unit) : RecyclerView.ViewHolder(cardItemView) {

        private val cardItemView = cardItemView
        private val cardItemTextView: TextView = cardItemView.findViewById(R.id.card_item_text_view)

        /* Bind data to view */
        fun bind(cardItem: CardItem) {
            cardItemTextView.text = cardItem.playerName

            // CHECK THIS AGAINST FLOWERS IMPLEMENTATIONS
            cardItemView.setOnClickListener { onClick(cardItem) }
        }
    }

    /* Creates and inflates view and returns CardItemViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardItemViewHolder(view, onClick)
    }

    /* Gets current CardItem and uses it to bind view */
    override fun onBindViewHolder(viewHolder: CardItemViewHolder, position: Int) {
        val cardItem = getItem(position)
        viewHolder.bind(cardItem)
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