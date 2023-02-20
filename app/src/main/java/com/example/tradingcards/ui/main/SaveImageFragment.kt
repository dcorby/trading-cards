package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.databinding.FragmentSaveImageBinding
import com.example.tradingcards.viewmodels.SaveImageViewModel


class SaveImageFragment : Fragment() {

    private var _binding: FragmentSaveImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainReceiver: MainReceiver
    private lateinit var viewModel: SaveImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainReceiver = requireActivity() as MainReceiver
        viewModel = ViewModelProvider(this).get(SaveImageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.id = arguments?.getString("id") ?: ""
        viewModel.name = arguments?.getString("name") ?: ""
        viewModel.link = arguments?.getString("link") ?: ""
        viewModel.width = arguments?.getInt("width") ?: -1
        viewModel.height = arguments?.getInt("height") ?: -1

        // For testing
        viewModel.link = "https://www.si.com/.image/t_share/MTY4MjYxMDk5MDc5NTQyMDM3/rickey-henderson-getty3jpg.jpg"
        viewModel.width = 766
        viewModel.height = 1200

        // Get frame dims
        Log.v("TEST", "width=${binding.frame.width}")
        Log.v("TEST", "height=${binding.frame.height}")
        // ^ Not reliable, because of issues like keyboard
        val frameWidth = mainReceiver.getScreenDims().getValue("width") - 32
        val frameHeight = mainReceiver.getScreenDims().getValue("width") - 32 - 100

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.henderi01)
        binding.image.setImageDrawable(drawable)
        //binding.image.setImageResource(R.drawable.henderi01)
    }
}