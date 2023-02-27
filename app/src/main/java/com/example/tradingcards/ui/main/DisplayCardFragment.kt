package com.example.tradingcards.ui.main

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.databinding.FragmentDisplayCardBinding
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.viewmodels.DisplayCardViewModel

class DisplayCardFragment : Fragment() {

    private var _binding: FragmentDisplayCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DisplayCardViewModel
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainReceiver = requireActivity() as MainReceiver
        dbManager = mainReceiver.getDBManager()
        viewModel = ViewModelProvider(this).get(DisplayCardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisplayCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.idx = arguments?.getInt("idx")!!
        viewModel.num = viewModel.idx + 1
        viewModel.ids = arguments?.getStringArrayList("ids")!!
        viewModel.id = viewModel.ids[viewModel.idx]

        // Set the image
        //val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.henderi01)
        val pathname = requireContext().filesDir.toString() + "/images/${viewModel.id}.jpg"
        val drawable = Drawable.createFromPath(pathname)
        binding.image.setImageDrawable(drawable)

        // Set the title
        binding.status.text = "${viewModel.num} of ${viewModel.ids.size}"

        // Handle close
        binding.close.setOnClickListener {
            // Parent fragment is DisplaySet
            requireParentFragment().parentFragmentManager.beginTransaction().remove(requireParentFragment()).commit()
            requireParentFragment().parentFragmentManager.popBackStack()
        }

        var isFront = true
        val front = AnimatorInflater.loadAnimator(requireContext(), R.animator.front) as AnimatorSet
        val back = AnimatorInflater.loadAnimator(requireContext(), R.animator.back) as AnimatorSet

        fun flip() {
            if (isFront) {
                front.setTarget(binding.front)
                back.setTarget(binding.back)
                front.start()
                back.start()
                isFront = false
            } else {
                front.setTarget(binding.back)
                back.setTarget(binding.front)
                back.start()
                front.start()
                isFront = true
            }
        }

        binding.front.setOnClickListener{ flip() }
        binding.back.setOnClickListener{ flip() }
    }
}