package com.example.tradingcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.CardItem
import com.example.tradingcards.CardItemsAdapter
import com.example.tradingcards.databinding.FragmentDisplaySetBinding

class DisplaySetFragment : Fragment() {

    lateinit var cardItemsAdapter: CardItemsAdapter

    private var _binding: FragmentDisplaySetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplaySetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardItemsAdapter = CardItemsAdapter { cardItem -> adapterOnClick(cardItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = cardItemsAdapter
    }

    private fun adapterOnClick(cardItem: CardItem) {

    }

}