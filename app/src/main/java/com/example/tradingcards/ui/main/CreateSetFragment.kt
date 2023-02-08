package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
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

    lateinit var locationsAdapter: LocationsAdapter
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

        // Get the directory
        viewModel.directory = arguments?.getString("directory", "") ?: ""

        // Get the path
        val parts = listOf(requireContext().filesDir.absolutePath, viewModel.directory, viewModel.name)
        val path: String = parts.filter { !it.equals("") }.joinToString("/")
        viewModel.path = path

        // Get the name, listen for edits
        binding.nameEditText.setText(viewModel.name, TextView.BufferType.EDITABLE)
        binding.nameEditText.addTextChangedListener {
            viewModel.name = binding.nameEditText.text.toString()
        }

        // Get the locations
        val locationItems = getLocationItems()
        locationsAdapter = LocationsAdapter { locationItem -> adapterOnClick(locationItem) }
        val recyclerView: RecyclerView = binding.locationRecyclerView
        recyclerView.adapter = locationsAdapter
        locationsAdapter.submitList(locationItems)

        // Create
        binding.create.setOnClickListener {
            createSet()
        }
    }

    private fun adapterOnClick(locationItem: LocationItem) {

    }

    private fun getLocationItems() : MutableList<LocationItem> {
        val locationItems = mutableListOf<LocationItem>()
        val file = File(viewModel.path).walkTopDown().forEach {
            val locationItem = LocationItem(it.absolutePath)
            locationItems.add(locationItem)
        }
        return locationItems
    }

    private fun createSet() {
        val file = File(viewModel.path)
        if (!file.exists()) {
            file.mkdirs()
            val bundle = Bundle()
            bundle.putString("directory", file.parent?.toString() ?: "")
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_CreateSetFragment_to_SetFragment, bundle)
        }
    }
}