package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.adapters.PlayerAdapter
import com.example.tradingcards.adapters.SetAdapter
import com.example.tradingcards.databinding.FragmentSelectPlayerBinding
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.items.PlayerItem
import com.example.tradingcards.items.SetItem
import com.example.tradingcards.viewmodels.SelectPlayerViewModel
import com.example.tradingcards.viewmodels.SetViewModel
import kotlinx.coroutines.*

class SelectPlayerFragment : Fragment() {

    private var _binding: FragmentSelectPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SelectPlayerViewModel
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager

    lateinit var playerAdapter: PlayerAdapter
    lateinit var tracker: SelectionTracker<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SelectPlayerViewModel::class.java)
        mainReceiver = requireActivity() as MainReceiver
        dbManager = mainReceiver.getDBManager()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerAdapter = PlayerAdapter { playerItem -> adapterOnClick(playerItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = playerAdapter

        // Init the selection library
        tracker = SelectionTracker.Builder<String>(
            "selectionItem",
            binding.recyclerView,
            PlayerItemKeyProvider(playerAdapter),
            PlayerItemDetailsLookup(binding.recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        playerAdapter.tracker = tracker

        // Watch for location selection
        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                }
                override fun onItemStateChanged(key: String, selected: Boolean) {
                    if (tracker.hasSelection()) { }
                    super.onItemStateChanged(key, !selected)
                }
            })

        // Get the current directory
        viewModel.currentDirectory = arguments?.getString("currentDirectory", "") ?: ""
        requireActivity().title = "Select Player (${viewModel.currentDirectory})"

        // Get the set data, to find out the source
        val set = dbManager.fetch("SELECT * FROM sets WHERE path = ?", arrayOf(viewModel.currentDirectory))[0]
        val source = set.getValue("source").toString()

        binding.editText.addTextChangedListener {
            if (viewModel.job.isActive) {
                viewModel.job.cancel()
            }
            viewModel.job = viewModel.viewModelScope.launch(Dispatchers.IO) {
                withTimeout(5000) {
                    val params = arrayOf(source, it.toString() + "%")
                    val players = dbManager.fetch("SELECT * FROM players WHERE source = ? AND name LIKE ?", params)
                    //delay(2000)

                    playerAdapter.submitList(players)
                    //binding.recyclerView.

                }
            }
            viewModel.job.invokeOnCompletion {

            }
        }
    }

    private fun adapterOnClick(playerItem: PlayerItem) { }
}

// Classes for the selection tracker
class PlayerItemKeyProvider(private val playerAdapter: PlayerAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String {
        return playerAdapter.currentList[position].name
    }
    override fun getPosition(key: String): Int {
        return playerAdapter.currentList.indexOfFirst { it.name == key }
    }
}

class PlayerItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as PlayerAdapter.PlayerItemViewHolder).getItemDetails()
        }
        return null
    }
}