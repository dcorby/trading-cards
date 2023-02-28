package com.example.tradingcards.ui.main

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.*
import com.example.tradingcards.adapters.PlayerAdapter
import com.example.tradingcards.databinding.FragmentSelectPlayerBinding
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.items.PlayerItem
import com.example.tradingcards.viewmodels.SelectPlayerViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import androidx.core.view.children

class SelectPlayerFragment : Fragment() {

    private var _binding: FragmentSelectPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SelectPlayerViewModel
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var toolbar: Toolbar

    var inAnimation: AlphaAnimation? = null
    var outAnimation: AlphaAnimation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SelectPlayerViewModel::class.java)
        mainReceiver = requireActivity() as MainReceiver
        dbManager = mainReceiver.getDBManager()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerAdapter = PlayerAdapter { playerItem -> adapterOnClick(playerItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = playerAdapter

        // Get the current directory
        viewModel.currentDirectory = arguments?.getString("currentDirectory") ?: ""
        toolbar = requireActivity().findViewById(R.id.toolbar) as Toolbar
        setToolbar()

        // Get the set data, to find out the source
        val set = dbManager.fetch("SELECT * FROM sets WHERE path = ?", arrayOf(viewModel.currentDirectory))[0]
        viewModel.source = set.getValue("source").toString()
        binding.label.text = viewModel.source

        // Populate the batches menu
        val batches = parseSources()
        val currentBatches = dbManager.fetch("SELECT * FROM sources WHERE id = ? AND date IS NOT NULL",
            arrayOf(viewModel.source), "batch") as MutableList<String>
        var verticalLayout: LinearLayout? = null
        batches.keys.sorted().reversed().forEachIndexed { index, batch ->
            if (verticalLayout == null) {
                verticalLayout = getVerticalLayout()
            }
            val view = layoutInflater.inflate(R.layout.item_season, null)
            val checkbox = view.findViewById<CheckBox>(R.id.checkbox)
            checkbox.tag = batch.toString()
            if (currentBatches.contains(batch.toString())) {
                checkbox.isChecked = true
            }
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                val batch = buttonView.tag.toString()
                if (isChecked && !currentBatches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightgreen))
                    viewModel.toAdd.add(batch)
                }
                if (isChecked && currentBatches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    viewModel.toRemove.remove(batch)
                }
                if (!isChecked && currentBatches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightred))
                    viewModel.toRemove.add(batch)
                }
                if (!isChecked && !currentBatches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    viewModel.toAdd.remove(batch)
                }
            }
            val textview = view.findViewById<TextView>(R.id.textview)
            textview.text = batch.toString()
            verticalLayout!!.addView(view)
            if (verticalLayout!!.childCount >= 10 || index == batches.keys.size - 1) {
                binding.batches.addView(verticalLayout)
                verticalLayout = null
            }
        }

        // Sync seasons
        binding.sync.setOnClickListener {

            // https://stackoverflow.com/questions/18021148/display-a-loading-overlay-on-android-screen
            inAnimation = AlphaAnimation(0f, 1f)
            inAnimation!!.duration = 200
            binding.progressFrame.animation = inAnimation
            binding.progressFrame.visibility = View.VISIBLE

            // Get the seasons to remove
            viewModel.toRemove.forEach { batch ->
                dbManager.exec("DELETE FROM players_batches WHERE source = ? AND batch = ?", arrayOf(viewModel.source, batch))
                dbManager.exec("UPDATE sources SET date = NULL WHERE id = ? AND batch = ?", arrayOf(viewModel.source, batch))
                viewModel.toRemove.clear()
            }

            // Get the seasons to add
            if (viewModel.toAdd.size == 0) {
                outAnimation = AlphaAnimation(1f, 0f)
                outAnimation!!.duration = 200
                binding.progressFrame.animation = outAnimation
                binding.progressFrame.visibility = View.GONE
            } else {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    withTimeout(10000) {
                        viewModel.toAdd.forEach { batch ->
                            syncPlayers(batch, batches.getValue(batch), batch == viewModel.toAdd.last()) { isFinal ->
                                if (isFinal) {
                                    outAnimation = AlphaAnimation(1f, 0f)
                                    outAnimation!!.duration = 200
                                    binding.progressFrame.animation = outAnimation
                                    binding.progressFrame.visibility = View.GONE
                                    viewModel.toAdd.forEach { batch ->
                                        currentBatches.add(batch)
                                    }
                                    viewModel.toRemove.forEach { batch ->
                                        currentBatches.remove(batch)
                                    }
                                    binding.batches.children.forEach { child ->
                                        (child as ViewGroup).children.forEach { ll ->
                                            (ll as ViewGroup).children.first()
                                                .setBackgroundColor(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.white
                                                    )
                                                )
                                        }
                                    }
                                    viewModel.toAdd.clear()
                                }
                            }
                        }
                    }
                }
            }
        }

        // Toggle the batches menu
        binding.toggle.setOnClickListener {
            if (binding.wrapper.visibility == View.VISIBLE) {
                binding.wrapper.visibility = View.GONE
                binding.toggle.text = "[+]"
            } else {
                binding.wrapper.visibility = View.VISIBLE
                binding.toggle.text = "[-]"
            }
        }

        binding.search.addTextChangedListener { text ->
            if (viewModel.job.isActive) {
                viewModel.job.cancel()
            }
            var players: MutableList<PlayerItem> = mutableListOf()
            viewModel.job = viewModel.viewModelScope.launch(Dispatchers.IO) {
                withTimeout(5000) {
                    if (text.toString() != "") {
                        val params = arrayOf(viewModel.source, text.toString() + "%")
                        players =
                            dbManager.fetch("SELECT * FROM players WHERE source = ? AND name LIKE ?", params).map {
                                val id = it.getValue("id").toString()
                                val name = it.getValue("name").toString()
                                val imagePath =
                                    requireContext().filesDir.toString() + viewModel.currentDirectory + id + ".jpg"
                                val hasImage = File(imagePath).exists()
                                PlayerItem(id, name, hasImage)
                            }.toMutableList()
                    }
                    // Switch back to the main application thread
                    withContext(Dispatchers.Main) {
                        playerAdapter.submitList(players)
                    }
                }
            }
            viewModel.job.invokeOnCompletion { }
        }
    }

    private fun getVerticalLayout() : LinearLayout {
        val layout = LinearLayout(requireContext())
        layout.layoutParams = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        layout.orientation = LinearLayout.VERTICAL
        return layout
    }

    private fun adapterOnClick(playerItem: PlayerItem) {
        val bundle = Bundle()
        bundle.putString("id", playerItem.id.toString())
        bundle.putString("name", playerItem.name)
        bundle.putString("currentDirectory", viewModel.currentDirectory)

        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.action_SelectPlayerFragment_to_SelectImageFragment, bundle)
    }

    private fun setToolbar() {
        toolbar.children.forEach { view ->
            if (view.tag == "title") {
                toolbar.removeView(view)
            }
        }
        toolbar.addView(Utils.getTitleView(requireContext(), null, "Select Player"))
    }

    private fun parseSources() : HashMap<String, MutableList<String>> {
        val data = HashMap<String, MutableList<String>>()

        val jsonObject = JSONObject(Utils.readAssetsFile(requireContext(), "sources.json"))
        val sources = Sources.toMap(jsonObject)
        val source = sources[viewModel.source] as HashMap<*, *>
        val batches = source["batches"] as List<*>

        batches.forEach {
            val batch = it as HashMap<*, *>
            val urls = mutableListOf<String>()
            (batch["urls"] as List<*>).forEach { url ->
                urls.add(url.toString())
            }
            data[batch["label"].toString()] = urls
        }
        return data
    }

    private suspend fun syncPlayers(batch: String, urls: MutableList<String>, isFinal: Boolean, callback: ((Boolean) -> Unit)) {
        val players: HashMap<String, String> = HashMap<String, String>()
        urls.forEach { url ->
            val response = URL(url)
            val content = response.readText()
            val tableMatches = FindAll.get("(stats_table\" id=\"players_standard.*?</table>)", content)
            val table = tableMatches[0][0]
            val playerMatches = FindAll.get("<a.*?/players/.*?/(.*?).shtml\">(.*?)</a>", table)
            playerMatches.forEach {
                players[it[0]] = it[1].replace("&nbsp;", " ")
            }
        }

        // Delete current players_batches
        mainReceiver.getDBManager().exec(
            "DELETE FROM players_batches WHERE source = ? AND batch = ?",
            arrayOf(viewModel.source, batch)
        )

        players.forEach { player ->
            val id = player.key
            val name = player.value

            // Update players
            mainReceiver.getDBManager().exec(
                "REPLACE INTO players(source, id, name) VALUES (?, ?, ?)",
                arrayOf(viewModel.source, id, name)
            )

            // Insert to players_batches
            val contentValues = ContentValues()
            contentValues.put("source", viewModel.source)
            contentValues.put("id", id)
            contentValues.put("batch", batch)
            dbManager.insert("players_batches", contentValues)
        }

        // Update sources
        val date =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time).toString()
        val contentValues = ContentValues()
        contentValues.put("date", date)
        dbManager.update(
            "sources",
            contentValues,
            "id = ? AND batch = ?",
            arrayOf(viewModel.source, batch)
        )

        // Prune players
        dbManager.exec(
            """
DELETE FROM players
WHERE source = ?
AND id NOT IN (SELECT DISTINCT id 
     FROM players_batches 
     WHERE source = ?)
""", arrayOf(viewModel.source, viewModel.source)
        )

        withContext(Dispatchers.Main) {
            callback(isFinal)
        }
    }
}