package com.example.tradingcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
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
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.viewmodels.SetViewModel
import java.io.File

class SetFragment : Fragment() {

    private var _binding: FragmentSetBinding? = null
    private val binding get() = _binding!!
    private lateinit var setAdapter: SetAdapter
    private lateinit var viewModel: SetViewModel
    private lateinit var tracker: SelectionTracker<String>
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainReceiver = requireActivity() as MainReceiver
        dbManager = mainReceiver.getDBManager()
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

        // Get the currentDirectory, and always display with a trailing slash
        viewModel.currentDirectory = arguments?.getString("currentDirectory", "") ?: ""
        if (viewModel.currentDirectory == "" || viewModel.currentDirectory.last().toString() != "/") {
            viewModel.currentDirectory += "/"
        }

        /*  The root directory is special and cannot contain cards, just other sets
            Thus, it has a null source
         */
        viewModel.source = if (viewModel.currentDirectory == "/") {
            null
        } else {
            dbManager.fetch(
                "SELECT * FROM sets WHERE path = ?",
                arrayOf(viewModel.currentDirectory),
                "source"
            )[0].toString()
        }

        setAdapter = SetAdapter { name -> adapterOnClick(name) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = setAdapter

        val currentSet = File(requireContext().filesDir.toString() + viewModel.currentDirectory)
        val files = currentSet.listFiles()
        if (viewModel.currentDirectory != "/" || files.isNotEmpty()) {
            requireActivity().title = ""
            val toolbar = requireActivity().findViewById(R.id.toolbar) as Toolbar
            toolbar.children.forEach { view ->
                if (view.tag == "title") {
                    toolbar.removeView(view)
                }
            }
            toolbar.addView(Utils.getTitleView(requireContext(), viewModel.currentDirectory, null))
            binding.listParent.visibility = View.VISIBLE

            val setItems = Utils.getSetItems(requireContext(), dbManager, viewModel.source, currentSet)
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
            //setAdapter.tracker = tracker

            // Watch for location selection
            tracker.addObserver(
                object : SelectionTracker.SelectionObserver<String>() {
                    override fun onSelectionChanged() {
                    }

                    override fun onItemStateChanged(key: String, selected: Boolean) {
                        if (tracker.hasSelection()) {
                        }
                        super.onItemStateChanged(key, !selected)
                    }
                })
        } else {
            requireActivity().title = "Get Started"
            binding.gettingStarted.visibility = View.VISIBLE
        }

        // Create
        fun create() {
            val bundle = Bundle()
            bundle.putString("currentDirectory", viewModel.currentDirectory)
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_SetFragment_to_CreateSetFragment, bundle)
        }
        binding.create.setOnClickListener { create() }
        binding.addSet.setOnClickListener { create() }

        // Add
        if (viewModel.currentDirectory == "/") {
            // Can't create or view cards in root, because it's not a set
            binding.addCard.visibility = View.GONE
            binding.view.visibility = View.GONE
        } else {
            binding.addCard.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("currentDirectory", viewModel.currentDirectory)
                val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.action_SetFragment_to_SelectPlayerFragment, bundle)
            }
        }
    }

    fun getSelectionName(): String? {
        if (tracker.selection.size() == 0) {
            Toast.makeText(requireContext(), "Select a directory", Toast.LENGTH_SHORT).show()
            return null
        }
        return tracker.selection.toList()[0]
    }

    // Click to open
    private fun adapterOnClick(name: String) {
        val bundle = Bundle()
        //val selectionName = getSelectionName()
        bundle.putString("currentDirectory", viewModel.currentDirectory + name)
        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.action_SetFragment_to_SetFragment, bundle)
    }
}


// Classes for the selection tracker
class SetItemKeyProvider(private val setAdapter: SetAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String {
        return setAdapter.currentList[position].filename
    }
    override fun getPosition(key: String): Int {
        return setAdapter.currentList.indexOfFirst { it.filename == key }
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