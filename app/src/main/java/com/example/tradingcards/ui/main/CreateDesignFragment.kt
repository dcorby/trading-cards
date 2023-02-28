package com.example.tradingcards.ui.main

import android.content.ContentValues
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.AdapterView
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.databinding.FragmentCreateDesignBinding
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.views.DataView
import com.example.tradingcards.views.PartnerView
import com.example.tradingcards.views.ShapeView
import com.skydoves.colorpickerview.listeners.ColorListener

class CreateDesignFragment : Fragment() {

    private var _binding: FragmentCreateDesignBinding? = null
    private val binding get() = _binding!!

    lateinit var activeView: PartnerView
    private lateinit var center: Pair<Int, Int>
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateDesignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainReceiver = requireActivity() as MainReceiver
        dbManager = mainReceiver.getDBManager()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = ""
        val toolbar = requireActivity().findViewById(R.id.toolbar) as Toolbar
        toolbar.children.forEach { view ->
            if (view.tag == "title") {
                toolbar.removeView(view)
            }
        }
        toolbar.addView(Utils.getTitleView(requireContext(), null, "Design Set"))

        // Layout designView
        binding.design.layoutParams = Utils.getLayoutParams("design", mainReceiver.getScreenDims())
        binding.controls.layoutParams.width = binding.design.layoutParams.width

        // Create a rectangle on click
        binding.rectangle.setOnClickListener {
            val shapeView = ShapeView(requireContext(), this)
            if (this::activeView.isInitialized) {
                activeView.anchors.hide()
            }
            activeView = shapeView
            binding.design.addView(shapeView)
            shapeView.show(center)
            shapeView.anchors.show(true)
            binding.design.addView(shapeView.anchors.left)
            binding.design.addView(shapeView.anchors.top)
            binding.design.addView(shapeView.anchors.right)
            binding.design.addView(shapeView.anchors.bottom)
        }

        // Init the color picker
        binding.colorPicker.setColorListener(ColorListener { color, fromUser ->
            if (this::activeView.isInitialized) {
                activeView.setBackgroundColor(color)
            }
        })

        // Get designView clicks, which will hide activeView anchors
        binding.design.setOnClickListener {
            if (this::activeView.isInitialized) {
                activeView.anchors.hide()
            }
        }

        // Get the designView width and height, in order to size added shapeViews
        // https://stackoverflow.com/questions/3591784/views-getwidth-and-getheight-returns-0
        binding.design.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                center = Pair(
                    binding.design.width / 2,
                    binding.design.height / 2
                )
            }
        })

        // Get data mappings selections
        binding.dataMappings.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position == 0) {
                    return
                }

                val label = resources.getStringArray(R.array.data_map_labels)[position]
                val tag = resources.getStringArray(R.array.data_map_ids)[position]
                val dataView = DataView(requireContext(), this@CreateDesignFragment, label)
                dataView.tag = tag
                if (this@CreateDesignFragment::activeView.isInitialized) {
                    activeView.anchors.hide()
                }
                activeView = dataView
                binding.design.addView(dataView)
                dataView.show(center)
                dataView.anchors.show(true)
                binding.design.addView(dataView.anchors.left)
                binding.design.addView(dataView.anchors.top)
                binding.design.addView(dataView.anchors.right)
                binding.design.addView(dataView.anchors.bottom)
                binding.dataMappings.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        // Save the view
        binding.save.setOnClickListener {
            val children = binding.design.children
            dbManager.beginTransaction()

            // Insert the card record
            val contentValues = ContentValues()
            contentValues.put("dummy", "") // dummy value just to get id autoincrement
            val card = dbManager.insert("cards", contentValues)

            children.filter { it is ShapeView || it is DataView }.forEachIndexed { index, view ->
                val params = view.layoutParams as RelativeLayout.LayoutParams
                val contentValues = ContentValues()
                val parent = view.parent as ViewGroup
                contentValues.put("card", card)
                if (view is ShapeView) {
                    contentValues.put("type", "ShapeView")
                }
                if (view is DataView) {
                    contentValues.put("type", "DataView")
                    contentValues.put("data", view.tag.toString())
                }
                contentValues.put("width", params.width / parent.layoutParams.width.toFloat())
                contentValues.put("height", params.height / parent.layoutParams.height.toFloat())
                contentValues.put("margin_left", params.leftMargin / parent.layoutParams.width.toFloat())
                contentValues.put("margin_top", params.topMargin / parent.layoutParams.height.toFloat())
                contentValues.put("hexadecimal", "#" + Integer.toHexString((view.background as ColorDrawable).color))
                val id = dbManager.insert("card_views", contentValues)
            }
            dbManager.commitTransaction()
            dbManager.endTransaction()
            this.parentFragmentManager.popBackStack()
        }
    }
}