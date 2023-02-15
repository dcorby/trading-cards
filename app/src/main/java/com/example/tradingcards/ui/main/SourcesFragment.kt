package com.example.tradingcards.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.adapters.LocationsAdapter
import com.example.tradingcards.adapters.SourceAdapter
import com.example.tradingcards.databinding.FragmentSourcesBinding
import com.example.tradingcards.items.LocationItem
import com.example.tradingcards.items.SourceItem
import com.example.tradingcards.viewmodels.CreateSetViewModel
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import java.net.URL
import java.util.concurrent.Executors

class SourcesFragment : Fragment() {

    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = _binding!!

    lateinit var mainReceiver: MainReceiver
    lateinit var sourceAdapter: SourceAdapter
    private lateinit var viewModel: CreateSetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(CreateSetViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainReceiver = requireActivity() as MainReceiver

        // Initialize the spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Baseball Reference", "FanGraphs"))
        binding.sourcesSpinner.adapter = adapter
        binding.sourcesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Get the sources data
        val sources = mainReceiver.getDBManager().fetch(
            "SELECT id, batch, CASE WHEN date IS NULL THEN 0 ELSE 1 END AS synced FROM sources",null)
        val sourcesData = HashMap<String, MutableList<SourceItem>>()
        sources.forEach { row ->
            val id = row.getValue("id").toString()
            if (!sourcesData.containsKey(id)) {
                sourcesData[id] = mutableListOf()
            }
            val sourceItem = SourceItem(row.getValue("batch").toString(), row.getValue("synced") == 1)
            sourcesData[id]!!.add(sourceItem)
        }

        sourceAdapter = SourceAdapter { sourceItem -> adapterOnClick(sourceItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = sourceAdapter
        sourceAdapter.submitList(sourcesData["baseball-reference"])

        // Sync
        binding.sync.setOnClickListener {
            syncSources()
        }
    }

    private fun adapterOnClick(sourceItem: SourceItem) {
        Log.v("TEST", "checkbox onclick")
    }

    private fun syncSources() {
        // AsyncTask is deprecated
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            // Outside the UI thread
            val foo = URL("http://www.google.com")
            foo.readText()


            handler.post {
                // Anything that requires the UI thread here
            }
        }

        // Alternative implementation with coroutines
        // https://stackoverflow.com/questions/44318859/fetching-a-url-in-android-kotlin-asynchronously
        // fun fetch_async(url: String, view: TextView) = launch(UI) {
        //     val result = async(CommonPool) { fetch_url(url) }
        //     view.text = result.await()
        // }
    }
}

