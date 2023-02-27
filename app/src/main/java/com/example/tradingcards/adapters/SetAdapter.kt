package com.example.tradingcards.adapters

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.items.SetItem

class SetAdapter(private val onClick: (SetItem) -> Unit) :
    ListAdapter<SetItem, SetAdapter.SetItemViewHolder>(SetItemDiffCallback) {

    inner class SetItemViewHolder(
            private val itemView: View,
            val onClick: (SetItem) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.text_view)
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        // Bind data to view
        fun bind(setItem: SetItem) {
            textView.text = setItem.label

            itemView.setOnClickListener {
                onClick(setItem)
            }

            imageView.setImageResource(
                if (setItem.isCard) {
                    R.drawable.ic_baseline_image_32_green
                } else {
                    R.drawable.ic_baseline_folder_32
                })
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

    override fun onViewRecycled(holder: SetItemViewHolder) {
        super.onViewRecycled(holder)
    }

// Implementation to distinguish click/long click, etc.
//    val onTouchListener = object : View.OnTouchListener {
//        private var handler = Handler(Looper.getMainLooper())
//        private var isPosting = false
//        private var currentView: View? = null
//
//        var callback = Runnable {
//            // Put the view in edit mode. Once it's in edit mode, it's locked to single clicks
//        }
//        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//            if (event == null || v == null) {
//                return true
//            }
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    if (!isPosting) {
//                        currentView = v
//                        handler.postDelayed(callback,500)
//                    }
//                    return true
//                }
//            }
//            when (event.action) {
//                MotionEvent.ACTION_CANCEL -> {
//                    handler.removeCallbacks(callback)
//                    isPosting = false
//                    if (!isLongClick) {
//                        val itemView = v.findViewById(R.id.text_view) as TextView
//                        onClick(itemView.text.toString())
//                    }
//                    return true
//                }
//            }
//            return true
//        }
//    }
}

object SetItemDiffCallback : DiffUtil.ItemCallback<SetItem>() {
    override fun areItemsTheSame(oldItem: SetItem, newItem: SetItem): Boolean {
        return oldItem.filename == newItem.filename
    }

    override fun areContentsTheSame(oldItem: SetItem, newItem: SetItem): Boolean {
        return oldItem.filename == newItem.filename
    }
}