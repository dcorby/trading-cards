package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.adapters.SetAdapter
import com.example.tradingcards.items.SetItem
import com.example.tradingcards.databinding.FragmentSetBinding
import com.example.tradingcards.viewmodels.SetViewModel
import java.io.File

class SetFragment : Fragment() {

    private var _binding: FragmentSetBinding? = null
    private val binding get() = _binding!!

    lateinit var setAdapter: SetAdapter
    private lateinit var viewModel: SetViewModel
    lateinit var tracker: SelectionTracker<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SetViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentDirectory = (arguments?.getString("currentDirectory", "") ?: "") + "/"
        setAdapter = SetAdapter { setItem -> adapterOnClick(setItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = setAdapter

        val setDir = File(requireContext().filesDir.toString() + viewModel.currentDirectory)
        val files = setDir.listFiles()
        if (viewModel.currentDirectory != "/" || files.isNotEmpty()) {
            requireActivity().setTitle(viewModel.currentDirectory)
            binding.listParent.visibility = View.VISIBLE

            val setItems = Utils.getSetItems(requireContext(), setDir)
            // Init the adapter with the locations
            setAdapter = SetAdapter { locationItem -> adapterOnClick(locationItem) }
            val recyclerView: RecyclerView = binding.recyclerView
            recyclerView.adapter = setAdapter
            setAdapter.submitList(setItems)

            // Init the selection library
            tracker = SelectionTracker.Builder<String>(
                "selectionItem",
                binding.recyclerView,
                SetItemKeyProvider(setAdapter),
                SetItemDetailsLookup(binding.recyclerView),
                StorageStrategy.createStringStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()
            setAdapter.tracker = tracker

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
        } else {
            requireActivity().setTitle("Get Started")
            binding.gettingStarted.visibility = View.VISIBLE
        }

        fun getSelectionName() : String? {
            if (tracker.selection.size() == 0) {
                Toast.makeText(requireContext(), "Select a directory", Toast.LENGTH_SHORT).show()
                return null
            }
            val name = tracker.selection.toList()[0]
            return name
        }

        fun create() {
            val bundle = Bundle()
            //val name = getSelectionName()
            //if (name == null) { return }
            //bundle.putString("currentDirectory", viewModel.currentDirectory + name)
            bundle.putString("currentDirectory", viewModel.currentDirectory)
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_SetFragment_to_CreateSetFragment, bundle)
        }
        binding.create1.setOnClickListener { create() }
        binding.create2.setOnClickListener { create() }

        binding.add.setOnClickListener {
            //val navController =
            //    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            //navController.navigate(R.id.action_SetFragment_to_CreateSetFragment)
        }
        binding.open.setOnClickListener {
            // Pressing open with nothing selected will open all cards in a stack
            val isDir = true
            if (isDir) {
                val bundle = Bundle()
                val name = getSelectionName()
                if (name == null) { return@setOnClickListener }
                bundle.putString("currentDirectory", viewModel.currentDirectory + name)
                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.action_SetFragment_to_SetFragment, bundle)
            }
        }
    }

    private fun adapterOnClick(setItem: SetItem) {
    }
}

// Classes for the selection tracker
class SetItemKeyProvider(private val setAdapter: SetAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String {
        return setAdapter.currentList[position].name
    }
    override fun getPosition(key: String): Int {
        return setAdapter.currentList.indexOfFirst { it.name == key }
    }
}

class SetItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as SetAdapter.SetItemViewHolder).getItemDetails()
        }
        return null
    }
}