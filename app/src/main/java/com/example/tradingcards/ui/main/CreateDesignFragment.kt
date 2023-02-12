package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.tradingcards.RectangleView
import com.example.tradingcards.databinding.FragmentCreateDesignBinding
import com.skydoves.colorpickerview.listeners.ColorListener

class CreateDesignFragment : Fragment() {

    private var _binding: FragmentCreateDesignBinding? = null
    private val binding get() = _binding!!

    lateinit var activeView: RectangleView
    private lateinit var origin: Pair<Int, Int>

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

        // Set title
        //requireActivity().title = "Create Design"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create a rectangle on click
        binding.rectangle.setOnClickListener {
            val rectangleView = RectangleView(requireContext(), this)
            if (this::activeView.isInitialized) {
                activeView.anchors.hide()
            }
            activeView = rectangleView
            binding.designView.addView(rectangleView)
            rectangleView.show(origin)
            rectangleView.anchors.show(true)
            binding.designView.addView(rectangleView.anchors.left)
            binding.designView.addView(rectangleView.anchors.top)
            binding.designView.addView(rectangleView.anchors.right)
            binding.designView.addView(rectangleView.anchors.bottom)
        }

        // Init the color picker
        binding.colorPicker.setColorListener(ColorListener { color, fromUser ->
            //val linearLayout: LinearLayout = findViewById(R.id.linearLayout)
            //linearLayout.setBackgroundColor(color)
        })

        // Get designView clicks, which will hide activeView anchors
        binding.designView.setOnClickListener {
            activeView.anchors.hide()
        }

        // Get the designView width and height, in order to size added rectangleViews
        // https://stackoverflow.com/questions/3591784/views-getwidth-and-getheight-returns-0
        binding.designView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                origin = Pair(
                    binding.designView.width / 2 - 50,
                    binding.designView.height / 2 - 50
                )
            }
        })

        // Save the view
        binding.save.setOnClickListener {
            val children = binding.designView.children

        }
    }
}