package com.example.tradingcards.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.tradingcards.R
import com.example.tradingcards.databinding.FragmentCreateSetBinding
import com.example.tradingcards.viewmodels.CreateSetViewModel
import java.io.File

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

        viewModel.directory = arguments?.getString("directory", "") ?: ""

        binding.name.setText(viewModel.name, TextView.BufferType.EDITABLE)
        binding.name.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            viewModel.name = binding.name.text.toString()
        }

        binding.create.setOnClickListener {
            createSet()
        }
    }

    private fun createSet() {
        val pathStr: String = requireContext().filesDir.absolutePath + "/" + viewModel.name
        val file = File(pathStr)
        if (!file.exists()) {
            file.mkdirs()

            val bundle = Bundle()
            bundle.putString("directory", file.parent?.toString() ?: "")
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_CreateSetFragment_to_SetFragment, bundle)
        }
    }
}