package com.example.tradingcards.ui.main

import android.app.ActionBar.LayoutParams
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.AdapterView
import android.widget.RelativeLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.Utils
import com.example.tradingcards.designviews.RectangleView
import com.example.tradingcards.databinding.FragmentCreateDesignBinding
import com.example.tradingcards.designviews.DataView
import com.example.tradingcards.designviews.PartnerView
import com.skydoves.colorpickerview.listeners.ColorListener


class CreateDesignFragment : Fragment() {

    private var _binding: FragmentCreateDesignBinding? = null
    private val binding get() = _binding!!

    lateinit var activeView: PartnerView
    private lateinit var center: Pair<Int, Int>

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

        // Layout designView
        val mainReceiver = requireActivity() as MainReceiver
        binding.designView.layoutParams = Utils.getLayoutParams("design", mainReceiver.getScreenDims())

        // Create a rectangle on click
        binding.rectangle.setOnClickListener {
            val rectangleView = RectangleView(requireContext(), this)
            if (this::activeView.isInitialized) {
                activeView.anchors.hide()
            }
            activeView = rectangleView
            binding.designView.addView(rectangleView)
            rectangleView.show(center)
            rectangleView.anchors.show(true)
            binding.designView.addView(rectangleView.anchors.left)
            binding.designView.addView(rectangleView.anchors.top)
            binding.designView.addView(rectangleView.anchors.right)
            binding.designView.addView(rectangleView.anchors.bottom)
        }

        // Init the color picker
        binding.colorPicker.setColorListener(ColorListener { color, fromUser ->
            if (this::activeView.isInitialized) {
                activeView.setBackgroundColor(color)
            }
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
                center = Pair(
                    binding.designView.width / 2,
                    binding.designView.height / 2
                )
            }
        })

        // Get data mappings selections
        binding.dataMappings.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position == 0) {
                    return
                }

                val dataView = DataView(requireContext(), this@CreateDesignFragment, "Foo Bar")
                if (this@CreateDesignFragment::activeView.isInitialized) {
                    activeView.anchors.hide()
                }
                activeView = dataView
                binding.designView.addView(dataView)
                dataView.show(center)
                dataView.anchors.show(true)
                binding.designView.addView(dataView.anchors.left)
                binding.designView.addView(dataView.anchors.top)
                binding.designView.addView(dataView.anchors.right)
                binding.designView.addView(dataView.anchors.bottom)

                binding.dataMappings.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Save the view
        binding.save.setOnClickListener {
            val children = binding.designView.children
            children.filter { it is RectangleView }.forEachIndexed { index, view ->
                Log.v("TEST", "view=${index}")
                val params = view.layoutParams as RelativeLayout.LayoutParams
                Log.v("TEST", "marginLeft=${params.leftMargin}")
                Log.v("TEST", "marginTop=${params.topMargin}")
                Log.v("TEST", "width=${params.width}")
                Log.v("TEST", "height=${params.height}")
                Log.v("TEST", "color=#${Integer.toHexString((view.background as ColorDrawable).color)}")
                Log.v("TEST", "--------------------")
            }
        }
    }
}