package com.example.tradingcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.items.ImageItem
import com.squareup.picasso.Picasso

class ImageAdapter(private val onClick: (ImageItem) -> Unit) :
    ListAdapter<ImageItem, ImageAdapter.ImageItemViewHolder>(ImageItemDiffCallback) {

    inner class ImageItemViewHolder(imageItemView: View,
                                     val onClick: (ImageItem) -> Unit) : RecyclerView.ViewHolder(imageItemView) {

        private val imageItemView = imageItemView
        private val imageItemImageView: ImageView = imageItemView.findViewById(R.id.image_view)

        /* Bind data to view */
        fun bind(imageItem: ImageItem) {
            Picasso.get().load(imageItem.thumbnailLink).into(imageItemImageView)
            imageItemView.setOnClickListener {
                onClick(imageItem)
            }
        }
    }

    /* Creates and inflates view and returns SetItemViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageItemViewHolder(view, onClick)
    }

    /* Gets current ListItem and uses it to bind view */
    override fun onBindViewHolder(viewHolder: ImageItemViewHolder, position: Int) {
        val listItem = getItem(position)
        viewHolder.bind(listItem)
    }
}

object ImageItemDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.link == newItem.link
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.link == newItem.link
    }
}