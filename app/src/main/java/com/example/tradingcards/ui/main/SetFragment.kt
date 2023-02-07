package com.example.tradingcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tradingcards.databinding.FragmentSetBinding

class SetFragment : Fragment() {

    private val cardItemViewModel: CardItemViewModel by viewModels()
    lateinit var cardItemsAdapter: CardItemsAdapter

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

        cardItemsAdapter = CardItemsAdapter { sanFile -> adapterOnClick(sanFile) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = sanFilesAdapter
    }

}