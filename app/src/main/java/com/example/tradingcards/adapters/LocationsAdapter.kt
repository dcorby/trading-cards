package com.example.tradingcards.adapters

import android.graphics.Color
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
import com.example.tradingcards.items.LocationItem

class LocationsAdapter(private val onClick: (LocationItem) -> Unit) :
    ListAdapter<LocationItem, LocationsAdapter.LocationItemViewHolder>(LocationItemDiffCallback) {

    lateinit var tracker: SelectionTracker<String>
    var prevRelativePath = ""

    init {
        setHasStableIds(true)
    }

    inner class LocationItemViewHolder(locationItemView: View,
                                  val onClick: (LocationItem) -> Unit) : RecyclerView.ViewHolder(locationItemView) {

        private val locationItemView = locationItemView
        private val locationItemTextView: TextView = locationItemView.findViewById(R.id.text_view)

        /* Bind data to view */
        fun bind(locationItem: LocationItem) {
            locationItemTextView.text = locationItem.relativePath

            // CHECK THIS AGAINST FLOWERS IMPLEMENTATIONS
            //locationItemView.setOnClickListener { onClick(locationItem) }

            tracker.let {
                if (it.isSelected(getItem(position).relativePath)) {
                    if (getItem(position).relativePath != prevRelativePath) {
                        tracker.deselect(prevRelativePath)
                    }
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue1))
                    prevRelativePath = getItem(position).relativePath
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
                override fun getSelectionKey(): String = currentList[adapterPosition].relativePath
                override fun inSelectionHotspot(e: MotionEvent): Boolean { return true }
                // this will override any existing itemView.setOnClickListener()
            }
    }

    /* Creates and inflates view and returns SetItemViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationItemViewHolder(view, onClick)
    }

    /* Gets current ListItem and uses it to bind view */
    override fun onBindViewHolder(viewHolder: LocationItemViewHolder, position: Int) {
        val listItem = getItem(position)
        viewHolder.bind(listItem)
    }
}

object LocationItemDiffCallback : DiffUtil.ItemCallback<LocationItem>() {
    override fun areItemsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
        return oldItem.relativePath == newItem.relativePath
    }

    override fun areContentsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
        return oldItem.relativePath == newItem.relativePath
    }
}