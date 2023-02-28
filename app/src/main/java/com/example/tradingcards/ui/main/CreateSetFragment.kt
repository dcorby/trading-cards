package com.example.tradingcards.ui.main

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.adapters.LocationsAdapter
import com.example.tradingcards.databinding.FragmentCreateSetBinding
import com.example.tradingcards.items.LocationItem
import com.example.tradingcards.viewmodels.CreateSetViewModel
import com.example.tradingcards.views.MiniView
import java.io.File

class CreateSetFragment : Fragment() {

    private var _binding: FragmentCreateSetBinding? = null
    private val binding get() = _binding!!

    lateinit var mainReceiver: MainReceiver
    private lateinit var viewModel: CreateSetViewModel

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the current directory
        viewModel.currentDirectory = arguments?.getString("currentDirectory", "") ?: ""

        requireActivity().title = ""
        val toolbar = requireActivity().findViewById(R.id.toolbar) as Toolbar
        val actionBar: android.app.ActionBar? = requireActivity().actionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.children.forEach { view ->
            if (view.tag == "title") {
                toolbar.removeView(view)
            }
        }
        toolbar.addView(Utils.getTitleView(requireContext(), viewModel.currentDirectory, "Create Set"))

        // Get the current absolute path + name
        val pathParts = listOf(requireContext().filesDir.absolutePath, viewModel.currentDirectory, viewModel.name)
        val absolutePath: String = pathParts.filter { !it.equals("") }.joinToString("/")
        viewModel.absolutePath = absolutePath

        // Get the name and location, and listen for edits
        binding.name.setText(viewModel.name, TextView.BufferType.EDITABLE)
        binding.name.addTextChangedListener {
            viewModel.name = binding.name.text.toString().trim()
            binding.location.text = viewModel.currentDirectory + viewModel.name
        }

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

        // Add the designs
        binding.scrollviewLayout.layoutParams.height = miniParams.height
        val aspectRatio = mainReceiver.getScreenDims().getValue("width") / mainReceiver.getScreenDims().getValue("height")
        designs.forEachIndexed { index, design ->
            val miniView = MiniView(requireContext(), design, miniParams, aspectRatio)
            miniView.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_gray)
            binding.scrollviewLayout.addView(miniView)
        }

        // Create some spacing
        binding.scrollviewLayout.children.forEach {
            ((it as RelativeLayout).layoutParams as LinearLayout.LayoutParams).rightMargin = 50
        }

        // Select design
        binding.scrollviewLayout.children.forEach { miniView ->
            miniView.setOnClickListener {
                (binding.scrollview.getChildAt(0) as ViewGroup).getChildAt(viewModel.activeDesign)
                    .background = ContextCompat.getDrawable(requireContext(), R.drawable.border_gray)
                viewModel.activeDesign = (it.parent as ViewGroup).indexOfChild(it)
                it.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_black)
            }
        }

        // Set default design
        viewModel.activeDesign = 0
        (binding.scrollview.getChildAt(0) as ViewGroup).getChildAt(0).background =
            ContextCompat.getDrawable(requireContext(), R.drawable.border_black)

        // Show sources
        val sources = mainReceiver.getDBManager().fetch(
            "SELECT id, COUNT(*) AS count FROM sources WHERE date IS NOT NULL GROUP BY id HAVING COUNT(*) > 0", null, "id")
        if (sources.size == 0) {
            sources.add("Add a source")
            binding.sourcesSpinner.isEnabled = false
        }

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            sources)
        binding.sourcesSpinner.adapter = adapter
        binding.sourcesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Create set
        binding.createSet.setOnClickListener {
            createSet()
        }
    }

    private fun adapterOnClick(locationItem: LocationItem) { }

    private fun createSet() {
        val name = viewModel.name
        val activeDesign = viewModel.activeDesign
        val source= resources.getStringArray(R.array.source_ids)[binding.sourcesSpinner.selectedItemPosition]

        if (name == "" || source == null) {
            var msg = ""
            if (name == "") { msg = "Name required" }
            if (source == null) { msg = "Select a source" }
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            return
        }

        val path = viewModel.absolutePath + viewModel.name
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
            // Change the representation of path. Make relative and add a trailing slash
            val currentDirectory = file.toString().replace(requireContext().filesDir.toString(), "") + "/"
            val contentValues = ContentValues()
            contentValues.put("path", currentDirectory)
            contentValues.put("source", source)
            contentValues.put("design", activeDesign)
            mainReceiver.getDBManager().insert("sets", contentValues)

            val bundle = Bundle()
            bundle.putString("currentDirectory", currentDirectory)
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_CreateSetFragment_to_SetFragment, bundle)
        }
    }

    private fun getUserDesigns() : ArrayList<ArrayList<HashMap<String, Any?>>> {
        val userDesigns = arrayListOf<ArrayList<HashMap<String, Any?>>>()
        val dbManager = mainReceiver.getDBManager()
        val userDesignViews = dbManager.fetch("SELECT * FROM card_views ORDER BY card ASC", null)
        val currentDesign = ArrayList<HashMap<String, Any?>>()
        val previousView: HashMap<String, *>? = null
        userDesignViews.forEach { userDesignView ->
            if (previousView != null && userDesignView.getValue("card") != previousView.getValue("card")) {
                userDesigns.add(ArrayList(currentDesign))
            }
            userDesignView.set("type", "ShapeView")
            currentDesign.add(userDesignView as kotlin.collections.HashMap<String, Any?>)
        }
        if (currentDesign.size > 0) {
            userDesigns.add(ArrayList(currentDesign))
        }
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