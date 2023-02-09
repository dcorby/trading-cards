package com.example.tradingcards.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.example.tradingcards.databinding.FragmentCreateDesignBinding
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorListener
import com.example.tradingcards.R


class CreateDesignFragment : Fragment() {

    private var _binding: FragmentCreateDesignBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateDesignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(CreateDesignViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the title
        requireActivity().title = "Create Design"

        // Create a rectangle on click
        binding.rectangle.setOnClickListener {
            //val rectangleView = layoutInflater.inflate(R.layout.rectangle, binding.designView, false) as RelativeLayout
            val rectangleView = RelativeLayout(requireContext())
            val params = RelativeLayout.LayoutParams(100, 100)
            params.setMargins(100, 100, 0, 0)
            rectangleView.setBackgroundColor(Color.parseColor("#ff0000"));
            rectangleView.layoutParams = params
            binding.designView.addView(rectangleView)
        }

        // Init the color picker
        binding.colorPicker.setColorListener(ColorListener { color, fromUser ->
            //val linearLayout: LinearLayout = findViewById(R.id.linearLayout)
            //linearLayout.setBackgroundColor(color)
        })
    }
}