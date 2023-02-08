package com.example.tradingcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.items.SetItem
import com.example.tradingcards.adapters.SetsAdapter
import com.example.tradingcards.databinding.FragmentSetBinding
import java.io.File

class SetFragment : Fragment() {

    private var _binding: FragmentSetBinding? = null
    private val binding get() = _binding!!

    lateinit var setsAdapter: SetsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setsAdapter = SetsAdapter { setItem -> adapterOnClick(setItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = setsAdapter

        val rootDir = File(requireContext().filesDir, "")
        val files = rootDir.listFiles()
        if (files.isNotEmpty()) {
            binding.listParent.visibility = View.VISIBLE
        } else {
            binding.gettingStarted.visibility = View.VISIBLE
        }

        binding.create.setOnClickListener {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_SetFragment_to_CreateSetFragment)
        }
    }

    private fun adapterOnClick(setItem: SetItem) {

    }

}