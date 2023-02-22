package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.databinding.FragmentDisplayFrontBinding
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.viewmodels.DisplayFrontViewModel
import java.io.File

class DisplayFrontFragment : Fragment() {

    private var _binding: FragmentDisplayFrontBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DisplayFrontViewModel
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainReceiver = requireActivity() as MainReceiver
        dbManager = mainReceiver.getDBManager()
        viewModel = ViewModelProvider(this).get(DisplayFrontViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayFrontBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardNum = arguments?.getInt("cardNum")

        // Set the image
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.henderi01)
        binding.image.setImageDrawable(drawable)

        // Set the title
        binding.status.text = "${cardNum} of N"

        // Handle close
        binding.close.setOnClickListener {
            Toast.makeText(requireContext(), "Close!", Toast.LENGTH_SHORT).show()
        }
    }
}