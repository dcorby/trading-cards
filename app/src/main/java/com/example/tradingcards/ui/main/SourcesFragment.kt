package com.example.tradingcards.ui.main

import android.content.ContentValues
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.util.rangeTo
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Sources
import com.example.tradingcards.Utils
import com.example.tradingcards.adapters.LocationsAdapter
import com.example.tradingcards.adapters.SourceAdapter
import com.example.tradingcards.databinding.FragmentSourcesBinding
import com.example.tradingcards.items.LocationItem
import com.example.tradingcards.items.SourceItem
import com.example.tradingcards.viewmodels.CreateSetViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executors

class SourcesFragment : Fragment() {

    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = _binding!!

    lateinit var mainReceiver: MainReceiver
    lateinit var sourceAdapter: SourceAdapter
    lateinit var res: Resources
    private lateinit var viewModel: CreateSetViewModel
    private lateinit var sourcesData: HashMap<String, MutableList<SourceItem>>
    var activeId = "baseball-reference"

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
        res = resources

        // Initialize the spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            res.getStringArray(R.array.source_labels))
        binding.spinner.adapter = adapter

        val sourceIds = res.getStringArray(R.array.source_ids)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                activeId = sourceIds[position]
                updateList(activeId)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Get the sources data
        val sources = mainReceiver.getDBManager().fetch(
            "SELECT id, batch, CASE WHEN date IS NULL THEN 0 ELSE 1 END AS synced FROM sources",null)
        sourcesData = HashMap<String, MutableList<SourceItem>>()
        sources.forEach { row ->
            val id = row.getValue("id").toString()
            if (!sourcesData.containsKey(id)) {
                sourcesData[id] = mutableListOf()
            }
            val sourceItem = SourceItem(row.getValue("batch").toString(), row.getValue("synced") == 1)
            sourcesData[id]!!.add(sourceItem)
        }

        sourceAdapter = SourceAdapter { sourceItem -> adapterOnClick(sourceItem) }
        val recyclerView: RecyclerView = binding.recyclerview
        recyclerView.adapter = sourceAdapter
        updateList(activeId)

        // Sync
        binding.sync.setOnClickListener {
            syncSources() // TODO? Get a callback when all I/O operations are done, and pop backstack or whatever
            Log.v("TEST", "syncSources() has returned")
        }
    }

    private fun updateList(id: String) {
        sourceAdapter.submitList(sourcesData[id])
        sourceAdapter.notifyDataSetChanged()
    }

    private fun adapterOnClick(sourceItem: SourceItem) {
        Log.v("TEST", "Checkbox onclick")
    }

    private fun syncSources() {
        Log.v("TEST", "Syncing for activeId=${activeId}")

        // Get the urls to sync
        val jsonObject = JSONObject(Utils.readAssetsFile(requireContext(), "sources.json"))
        val sources = Sources.toMap(jsonObject)
        val source = sources[activeId] as HashMap<*, *>
        val batches = source["batches"] as List<*>
        batches.forEach {
            val batch = it as HashMap<*, *>
            if (batch["label"] == "2022") {
                (batch["urls"] as List<*>).forEach { url ->
                    Log.v("TEST", "url=${url}")
                }
            }
        }

        fun downloadUrl(url: String) {
            Log.v("TEST", "Downloading URL=$url")
            //Thread.sleep(10000)
        }

        // https://stackoverflow.com/questions/58170206/download-multiple-content-asynchronously-from-a-single-coroutine
        var urls = listOf("url1", "url2", "url3", "url4", "url5")

        var timer: CountDownTimer? = null
        var jobs = GlobalScope.launch(Dispatchers.IO) {
            val results = urls.map{ async { downloadUrl(it) } }
            results.awaitAll()
            timer?.cancel()
            Log.v("TEST", "Done awaiting")
        }
        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                jobs.cancel()
                throw Exception("downloadUrls timeout")
            }
        }.start()
    }
}

