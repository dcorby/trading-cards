package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout.LayoutParams
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MiniView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.adapters.LocationsAdapter
import com.example.tradingcards.databinding.FragmentCreateSetBinding
import com.example.tradingcards.items.LocationItem
import com.example.tradingcards.viewmodels.CreateSetViewModel
import java.io.File

// adb shell
// run-as com.example.tradingcards

class CreateSetFragment : Fragment() {

    private var _binding: FragmentCreateSetBinding? = null
    private val binding get() = _binding!!

    lateinit var mainReceiver: MainReceiver
    lateinit var locationsAdapter: LocationsAdapter
    private lateinit var viewModel: CreateSetViewModel
    lateinit var tracker: SelectionTracker<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateSetViewModel::class.java)
        // Set title
        //requireActivity().title = "Create Set"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the directory
        viewModel.currentDirectory = arguments?.getString("currentDirectory", "") ?: ""

        // Get the absolute path
        val pathParts = listOf(requireContext().filesDir.absolutePath, viewModel.currentDirectory, viewModel.name)
        val absolutePath: String = pathParts.filter { !it.equals("") }.joinToString("/")
        viewModel.absolutePath = absolutePath

        // Get the name, listen for edits
        binding.nameEditText.setText(viewModel.name, TextView.BufferType.EDITABLE)
        binding.nameEditText.addTextChangedListener {
            viewModel.name = binding.nameEditText.text.toString()
            binding.locationLiveView.text =
                Utils.getRelativePath(
                    requireContext(),
                    viewModel.absolutePath + "/" + tracker.selection + "/" + viewModel.name)
        }

        // Get the locations
        val locationItems = getLocationItems()

        // Init the adapter with the locations
        locationsAdapter = LocationsAdapter { locationItem -> adapterOnClick(locationItem) }
        val recyclerView: RecyclerView = binding.locationRecyclerView
        recyclerView.adapter = locationsAdapter
        locationsAdapter.submitList(locationItems)

        // Init the selection library
        tracker = SelectionTracker.Builder<String>(
            "selectionItem",
            binding.locationRecyclerView,
            LocationItemKeyProvider(locationsAdapter),
            LocationItemDetailsLookup(binding.locationRecyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        locationsAdapter.tracker = tracker

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
        tracker.select("/")

        // Create design
        binding.createDesign.setOnClickListener {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_CreateSetFragment_to_CreateDesignFragment)
        }

        // Display designs
        // It is safe to call requireActivity() here
        // https://stackoverflow.com/questions/65863267/is-it-safe-to-call-getactivity-from-oncreateview
        mainReceiver = requireActivity() as MainReceiver
        val screenDims = mainReceiver.getScreenDims()
        val designParams = Utils.getLayoutParams("design", screenDims)

        // Get the designs
        val designs = mutableListOf<MutableList<HashMap<String, Any?>>>()

        // Get the default design
        designs.add(mainReceiver.getDefaultDesign(designParams.width, designParams.height))

        // Get user-generated designs
        // TODO

        // Get the mini layoutParams
        val miniParams = Utils.getLayoutParams("mini", screenDims)

        // Get the shrinkFactor
        val shrinkFactor = 150 / designParams.width.toFloat()

        // Add the designs
        binding.designScrollview.layoutParams.height = miniParams.height
        designs.forEach { design ->
            val view = MiniView(requireContext(), design)
            view.layoutParams = LayoutParams(miniParams.width, miniParams.height)
            binding.designScrollview.addView(view.shrink(shrinkFactor))
        }

        // Select design

        // Create set
        binding.createSet.setOnClickListener {
            createSet()
        }
    }

    private fun adapterOnClick(locationItem: LocationItem) {
    }

    private fun getLocationItems() : MutableList<LocationItem> {
        val locationItems = mutableListOf<LocationItem>()
        File(viewModel.absolutePath).walkTopDown().forEach {
            if (it.isDirectory) {
                val locationItem = LocationItem(requireContext(), it.absolutePath)
                locationItems.add(locationItem)
            }
        }
        return locationItems
    }

    private fun createSet() {
        val file = File(viewModel.absolutePath)
        if (!file.exists()) {
            file.mkdirs()
            val bundle = Bundle()
            bundle.putString("currentDirectory", file.parent?.toString() ?: "")
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_CreateSetFragment_to_SetFragment, bundle)
        }
    }
}

// Classes for the selection tracker
class LocationItemKeyProvider(private val locationsAdapter: LocationsAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String {
        return locationsAdapter.currentList[position].relativePath
    }
    override fun getPosition(key: String): Int {
        return locationsAdapter.currentList.indexOfFirst { it.relativePath == key }
    }
}

class LocationItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as LocationsAdapter.LocationItemViewHolder).getItemDetails()
        }
        return null
    }
}