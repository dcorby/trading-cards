package com.example.tradingcards.ui.main

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
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

        // Get the current directory
        viewModel.currentDirectory = arguments?.getString("currentDirectory", "") ?: ""

        // Get the current absolute path + name
        val pathParts = listOf(requireContext().filesDir.absolutePath, viewModel.currentDirectory, viewModel.name)
        val absolutePath: String = pathParts.filter { !it.equals("") }.joinToString("/")
        viewModel.absolutePath = absolutePath

        // Get the name and location, and listen for edits
        binding.nameEditText.setText(viewModel.name, TextView.BufferType.EDITABLE)
        binding.nameEditText.addTextChangedListener {
            viewModel.name = binding.nameEditText.text.toString().trim()
            binding.locationLiveView.text = tracker.selection.toList()[0] + viewModel.name
            viewModel.location = requireContext().filesDir.absolutePath + "/" + binding.locationLiveView.text
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
        binding.designAdd.setOnClickListener {
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
        val designs = arrayListOf<ArrayList<HashMap<String, Any?>>>()

        // Get the default design
        designs.add(mainReceiver.getDefaultDesign(designParams.width, designParams.height))

        // Get user-generated designs
        designs += getUserDesigns()

        // Get the mini layoutParams
        val miniParams = Utils.getLayoutParams("mini", screenDims)

        // Get the shrinkFactor
        val shrinkFactor = 150 / designParams.width.toFloat()

        // Add the designs
        binding.scrollviewLayout.layoutParams.height = miniParams.height
        designs.forEach { design ->
            val view = MiniView(requireContext(), design)
            view.layoutParams = LayoutParams(miniParams.width, miniParams.height)
            val miniView = view.shrink(shrinkFactor)
            miniView.tag = design[0]["card"] ?: 0
            binding.scrollviewLayout.addView(miniView)
        }

        // Select design
        binding.scrollviewLayout.children.forEach { miniView ->
            miniView.setOnClickListener {
                Toast.makeText(requireContext(), it.tag.toString(), Toast.LENGTH_SHORT).show()
                viewModel.card = it.tag.toString()
            }
        }

        // Show sources
        val sources = mainReceiver.getDBManager().fetch(
            "SELECT id, COUNT(*) AS count FROM sources WHERE date IS NOT NULL GROUP BY id HAVING COUNT(*) > 0", null, "id")
        if (sources.size == 0) {
            sources.add("Add a source")
            binding.sourcesManage.text = "Add"
            binding.sourcesSpinner.isEnabled = false
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sources)
        binding.sourcesSpinner.adapter = adapter
        binding.sourcesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Manage/add source
        binding.sourcesManage.setOnClickListener {
            //Toast.makeText(requireContext(), "Add source test", Toast.LENGTH_SHORT).show()
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_CreateSetFragment_to_SourcesFragment)
        }

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
        // Need a name, a design, and a source
        val name = viewModel.name
        val card= viewModel.card
        val source= resources.getStringArray(R.array.source_ids)[binding.sourcesSpinner.selectedItemPosition]

        if (name == "" || card == null || source == null) {
            var msg = ""
            if (name == "") { msg = "Name required" }
            if (card == null) { msg = "Select a design" }
            if (source == null) { msg = "Select a source" }
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            return
        }

        val path = requireContext().filesDir.absolutePath + "/" + binding.locationLiveView.text

        // TODO: Create the set here

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

    private fun getUserDesigns() : ArrayList<ArrayList<HashMap<String, Any?>>> {
        val userDesigns = arrayListOf<ArrayList<HashMap<String, Any?>>>()
        val dbManager = mainReceiver.getDBManager()
        val userDesignViews = dbManager.fetch("SELECT * FROM card_views ORDER BY card ASC", null)
        var currentDesign = ArrayList<HashMap<String, Any?>>()
        val previousView: HashMap<String, *>? = null
        userDesignViews.forEach { userDesignView ->
            if (previousView != null && userDesignView.getValue("card") != previousView.getValue("card")) {
                userDesigns.add(ArrayList(currentDesign))
            }

            // ************ TODO: This needs to be set on create **************
            userDesignView.set("type", "ShapeView")

            currentDesign.add(userDesignView as kotlin.collections.HashMap<String, Any?>)
        }
        userDesigns.add(ArrayList(currentDesign))
        return userDesigns
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