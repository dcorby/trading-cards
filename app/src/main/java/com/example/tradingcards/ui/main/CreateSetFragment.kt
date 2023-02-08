package com.example.tradingcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.databinding.FragmentCreateSetBinding
import com.example.tradingcards.viewmodels.CreateSetViewModel

class CreateSetFragment : Fragment() {

    private var _binding: FragmentCreateSetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateSetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateSetViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.name.setText(viewModel.name, TextView.BufferType.EDITABLE)
        binding.name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            viewModel.name = binding.name.text.toString()
        }
    }
}