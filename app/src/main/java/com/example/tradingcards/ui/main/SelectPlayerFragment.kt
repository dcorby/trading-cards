package com.example.tradingcards.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.adapters.PlayerAdapter
import com.example.tradingcards.databinding.FragmentSelectPlayerBinding
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.items.PlayerItem
import com.example.tradingcards.viewmodels.SelectPlayerViewModel
import kotlinx.coroutines.*
import java.io.File

class SelectPlayerFragment : Fragment() {

    private var _binding: FragmentSelectPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SelectPlayerViewModel
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var toolbar: Toolbar

    private val brYears = (1901..2022).toList()

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
        val source = set.getValue("source").toString()
        binding.label.text = source

        // Populate the batches menu
        val batches = dbManager.fetch("SELECT * FROM sources WHERE id = ?", arrayOf(source), "batch") as MutableList<String>
        var verticalLayout: LinearLayout? = null
        brYears.reversed().forEachIndexed { index, batch ->
            if (verticalLayout == null) {
                verticalLayout = getVerticalLayout()
            }
            val view = layoutInflater.inflate(R.layout.item_season, null)
            val checkbox = view.findViewById<CheckBox>(R.id.checkbox)
            checkbox.tag = batch.toString()
            if (batches.contains(batch.toString())) {
                checkbox.isChecked = true
            }
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                val batch = buttonView.tag.toString()
                if (isChecked && !batches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightgreen))
                }
                if (isChecked && batches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                if (!isChecked && batches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightred))
                }
                if (!isChecked && !batches.contains(batch)) {
                    (buttonView.parent as View).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }
            val textview = view.findViewById<TextView>(R.id.textview)
            textview.text = batch.toString()
            verticalLayout!!.addView(view)
            if (verticalLayout!!.childCount >= 10 || index == brYears.size - 1) {
                binding.batches.addView(verticalLayout)
                verticalLayout = null
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
                        val params = arrayOf(source, text.toString() + "%")
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
}