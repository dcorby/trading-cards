package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
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
        viewModel.currentDirectory = arguments?.getString("currentDirectory", "") ?: ""
        requireActivity().title = "Select Player (${viewModel.currentDirectory})"

        // Get the set data, to find out the source
        val set = dbManager.fetch("SELECT * FROM sets WHERE path = ?", arrayOf(viewModel.currentDirectory))[0]
        val source = set.getValue("source").toString()

        binding.editText.addTextChangedListener {
            if (viewModel.job.isActive) {
                viewModel.job.cancel()
            }
            var players: MutableList<PlayerItem> = mutableListOf()
            viewModel.job = viewModel.viewModelScope.launch(Dispatchers.IO) {
                withTimeout(5000) {
                    val params = arrayOf(source, it.toString() + "%")
                    players = dbManager.fetch("SELECT * FROM players WHERE source = ? AND name LIKE ?", params).map {
                        val id = it.getValue("id").toString()
                        val name = it.getValue("name").toString()
                        val imagePath = requireContext().filesDir.toString() + viewModel.currentDirectory + id + ".jpg"
                        val hasImage = File(imagePath).exists()
                        PlayerItem(id, name, hasImage)
                    }.toMutableList()
                    // players.add(0, PlayerItem("henderi01", "Rickey Henderson"))
                    // delay(2000)
                    // Switch back to the main application thread
                    withContext(Dispatchers.Main) {
                        playerAdapter.submitList(players)
                    }
                }
            }
            viewModel.job.invokeOnCompletion {
                // anything we need to do here??
            }
        }
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
}