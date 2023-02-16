package com.example.tradingcards.ui.main

import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Sources
import com.example.tradingcards.Utils
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

    //lateinit var players: HashMap<String, String>
    lateinit var matches: MutableList<Sequence<MatchResult>>


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
        var num = 0
        binding.sync.setOnClickListener {
            num++
            // https://developer.android.com/kotlin/coroutines
            // This should probably be attached to a viewModelScope
            // GlobalScope.launch(Dispatchers.IO) {

            //val response = URL("https://www.baseball-reference.com/leagues/majors/2022-standard-batting.shtml")
            //val content = response.readText()
//            Log.v("TEST", "read text")
//            var str = "<a href=\"/players/a/abramcj01.shtml\">CJ&nbsp;Abrams</a>"
//            val regex = Regex("<a.*?/players/(.*?).shtml\">(.*?)</a>")
//            regex.findAll(str, 0).forEach {
//                Log.v("TEST", "${it.groupValues[1]}, ${it.groupValues[2]}")
//            }

            if (num == 1) {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    //withTimeout(10000) {
                    //players = hashMapOf<String, String>()
                    /*  Can't operate on the sequence returned by Regex.findall()
                     *  in the coroutine, not sure why. Return a list of the sequences
                     *  and set the players hashmap here
                     */
                    matches = syncSources()
                    Log.v("TEST", "${matches.size}")
//                    try {
//                        Log.v("TEST", "${matches[0].toList().size}")
//                        Log.v("TEST", "${matches[1].toList().size}")
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                    //val matches = syncSources().map { it.toMutableList() }
                    //matches.forEach { batch ->
                    //batch.forEach { match ->
                    //players[match.groupValues[1]] = match.groupValues[2]
                    //}
                    //}
                    //}
                    Log.v("TEST", "Ok, players all loaded")
                    // Do db stuff...
                }
            }
            if (num == 2) {
                Log.v("TEST", "${matches.size}")
                val sequence0 = matches[0]
                //val sequence1 = matches[1]
                Log.v("TEST", "inspecting matches...")
                //Log.v("TEST", "${sequence0.count()}")
                //Log.v("TEST", "${sequence1.count()}")
                //sequence0.forEach {
                //  Log.v("TEST", "${it.groupValues[1]}")
                //}






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

    private suspend fun syncSources() : MutableList<Sequence<MatchResult>> {
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

        /*  See note at syncSources() invocation about why to
         *  return a list of sequences
         */
        val matches = mutableListOf<Sequence<MatchResult>>()
        fun downloadUrl(url: String) {
            val response = URL(url)
            val content = response.readText()
            Log.v("TEST", "${content.length}")
            Log.v("TEST", "${content.takeLast(10)}")
            //Log.v("TEST", content)
            //<a href="/players/a/abramcj01.shtml">CJ&nbsp;Abrams</a>
            val regex = Regex("<a.*?/players/.*?/(.*?).shtml\">(.*?)</a>")
            val tmp = regex.findAll(content)
            Log.v("TEST", "starting for each")
            val iter = tmp.iterator()
            while (iter.hasNext()) {
                val foo = iter.next()
                Log.v("TEST", foo.groupValues[1])
                Log.v("TEST", foo.groupValues[2])
            }

            Log.v("TEST", "ending for each")
            matches.add(tmp)
        }

        suspend fun getData() = coroutineScope {
            val results = urls.map{ async { downloadUrl(it) } }
            results.awaitAll()
        }
        getData()
        return matches
    }
}

