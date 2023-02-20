package com.example.tradingcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.databinding.FragmentSelectImageBinding
import com.example.tradingcards.viewmodels.SaveImageViewModel

class SaveImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SaveImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SaveImageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.id = arguments?.getString("id") ?: ""
        viewModel.name = arguments?.getString("name") ?: ""
        viewModel.link = arguments?.getString("link") ?: ""
        viewModel.width = arguments?.getInt("width") ?: -1
        viewModel.height = arguments?.getInt("height") ?: -1
    }
}