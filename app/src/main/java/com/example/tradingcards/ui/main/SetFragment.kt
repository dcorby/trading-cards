package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.SetItem
import com.example.tradingcards.SetItemsAdapter
import com.example.tradingcards.databinding.FragmentSetBinding
import java.io.File

class SetFragment : Fragment() {

    lateinit var setItemsAdapter: SetItemsAdapter

    private var _binding: FragmentSetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setItemsAdapter = SetItemsAdapter { setItem -> adapterOnClick(setItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = setItemsAdapter

        val rootDir = File(requireContext().filesDir, "")
        val files = rootDir.listFiles()
        if (files.isNotEmpty()) {
            binding.listParent.visibility = View.VISIBLE
        } else {
            binding.gettingStarted.visibility = View.VISIBLE
        }
    }

    private fun adapterOnClick(setItem: SetItem) {

    }

}