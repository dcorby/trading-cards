package com.example.tradingcards.ui.main

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.*
import com.example.tradingcards.adapters.SourceAdapter
import com.example.tradingcards.databinding.FragmentSourcesBinding
import com.example.tradingcards.items.SourceItem
import com.example.tradingcards.viewmodels.TestViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class SourcesFragment : Fragment() {

    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = _binding!!

    lateinit var mainReceiver: MainReceiver
    lateinit var sourceAdapter: SourceAdapter
    lateinit var res: Resources
    private lateinit var sourcesData: HashMap<String, MutableList<SourceItem>>
    var activeId = "baseball-reference"

    private lateinit var viewModel: TestViewModel

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
        viewModel = ViewModelProvider(this).get(TestViewModel::class.java)
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
            // https://developer.android.com/kotlin/coroutines
            // This should probably be attached to a viewModelScope
            // GlobalScope.launch(Dispatchers.IO) {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                withTimeout(10000) {
                    val players = syncSources()
                }
            }
        }
    }

    private fun updateList(id: String) {
        sourceAdapter.submitList(sourcesData[id])
        sourceAdapter.notifyDataSetChanged()
    }

    private fun adapterOnClick(sourceItem: SourceItem) {
        Log.v("TEST", "Checkbox onclick")
    }

    private suspend fun syncSources() : HashMap<String, String> {
        Log.v("TEST", "Syncing for activeId=${activeId}")

        val jsonObject = JSONObject(Utils.readAssetsFile(requireContext(), "sources.json"))
        val sources = Sources.toMap(jsonObject)
        val source = sources[activeId] as HashMap<*, *>
        val batches = source["batches"] as List<*>
        val urls = mutableListOf<String>()
        batches.forEach {
            val batch = it as HashMap<*, *>
            if (batch["label"] == "2022") {
                (batch["urls"] as List<*>).forEach { url ->
                    urls.add(url.toString())
                }
            }
        }

        val players = HashMap<String, String>()
        fun downloadUrl(url: String) {
            val response = URL(url)
            val content = response.readText()
            val matches = FindAll.get("<a.*?/players/.*?/(.*?).shtml\">(.*?)</a>", content)
            matches.forEach {
                players[it.first] = it.second
                Log.v("TEST", it.first)
                Log.v("TEST", it.second)
            }
        }

        suspend fun getData() = coroutineScope {
            val results = urls.map{ async { downloadUrl(it) } }
            results.awaitAll()
        }
        getData()
        return players
    }
}