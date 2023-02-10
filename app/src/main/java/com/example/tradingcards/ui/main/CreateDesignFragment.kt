package com.example.tradingcards.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import androidx.core.view.contains
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.example.tradingcards.CircleView
import com.example.tradingcards.databinding.FragmentCreateDesignBinding
import com.skydoves.colorpickerview.listeners.ColorListener

class CreateDesignFragment : Fragment() {

    private var _binding: FragmentCreateDesignBinding? = null
    private val binding get() = _binding!!

    private lateinit var activeView: RelativeLayout

    private lateinit var anchors: Anchors
    class Anchors(context: Context) {
        val left = CircleView(context)
        val top = CircleView(context)
        val right = CircleView(context)
        val bottom = CircleView(context)
        val leftParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val topParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val rightParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val bottomParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        anchors = Anchors(context)
    }

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
            rectangleView.setBackgroundColor(Color.parseColor(getRandomColor()));
            rectangleView.layoutParams = params
            activeView = rectangleView
            binding.designView.addView(rectangleView)
            drawAnchors()
        }

        // Init the color picker
        binding.colorPicker.setColorListener(ColorListener { color, fromUser ->
            //val linearLayout: LinearLayout = findViewById(R.id.linearLayout)
            //linearLayout.setBackgroundColor(color)
        })
    }

    private fun drawAnchors() {

        val onTouchListener = object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val x = event?.x
                anchors.rightParams.leftMargin = 300
                anchors.right.layoutParams = anchors.rightParams
                return true
            }
        }

        /* "anything you post to the queue will happen after the layout pass"
           https://stackoverflow.com/questions/3591784/views-getwidth-and-getheight-returns-0
         */
        binding.designView.post {
            // Check whether the circles have been added
            val isInit = !binding.designView.contains(anchors.left)
            if (isInit) {
                binding.designView.addView(anchors.left)
                binding.designView.addView(anchors.top)
                binding.designView.addView(anchors.right)
                binding.designView.addView(anchors.bottom)
                anchors.left.setOnTouchListener(onTouchListener)
                anchors.top.setOnTouchListener(onTouchListener)
                anchors.right.setOnTouchListener(onTouchListener)
                anchors.bottom.setOnTouchListener(onTouchListener)
            }

            // left
            anchors.leftParams.leftMargin = activeView.marginLeft - 10
            anchors.leftParams.topMargin = activeView.marginTop + (activeView.height / 2) - 10
            anchors.left.layoutParams = anchors.leftParams

            // top
            anchors.topParams.leftMargin = activeView.marginLeft + (activeView.width / 2) - 10
            anchors.topParams.topMargin = activeView.marginTop - 10
            anchors.top.layoutParams = anchors.topParams

            // right
            anchors.rightParams.leftMargin = activeView.marginLeft + activeView.width - 10
            anchors.rightParams.topMargin = activeView.marginTop + (activeView.height / 2) - 10
            anchors.right.layoutParams = anchors.rightParams

            // bottom
            anchors.bottomParams.leftMargin = activeView.marginLeft + (activeView.width / 2) - 10
            anchors.bottomParams.topMargin = activeView.marginTop + activeView.height - 10
            anchors.bottom.layoutParams = anchors.bottomParams
        }
    }

    private fun getRandomColor() : String {
        val colors = listOf<String>("#DFFF00", "#FFBF00", "#FF7F50", "#DE3163", "#9FE2BF", "#40E0D0", "#6495ED", "#CCCCFF", "#FFA500", "#EEE8AA", "#FF00FF", "#6A5ACD", "#90EE90", "#808000")
        return colors.random()
    }

    private fun onTouchListener(v: View, event: MotionEvent) {

    }
}