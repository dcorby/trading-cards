package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
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
        viewModel.width = (arguments?.getInt("width") ?: 0).toFloat()
        viewModel.height = (arguments?.getInt("height") ?: 0).toFloat()

        // For testing
        viewModel.link = "https://www.si.com/.image/t_share/MTY4MjYxMDk5MDc5NTQyMDM3/rickey-henderson-getty3jpg.jpg"
        viewModel.width = 766.toFloat()
        viewModel.height = 1200.toFloat()
        // Fire is an HDPI device, and width will be ~511 dp
        // https://stackoverflow.com/questions/2025282/what-is-the-difference-between-px-dip-dp-and-sp

        // Get frame dims
        // Log.v("TEST", "width=${binding.frame.width}")
        // Log.v("TEST", "height=${binding.frame.height}")
        // ^ Not reliable, because onDraw() might come later, and because of keyboard
        val frameWidth = (mainReceiver.getScreenDims().getValue("width") - 32).toFloat()
        val frameHeight = (mainReceiver.getScreenDims().getValue("height") - 32 - 100).toFloat()

        Log.v("TEST", "frameWidth=${frameWidth}")
        Log.v("TEST", "frameHeight=${frameHeight}")

        // Get image's pixel dims
        val origWidth = Utils.convertPxToDp(requireContext(), viewModel.width.toFloat())
        val origHeight = Utils.convertPxToDp(requireContext(), viewModel.height.toFloat())
        Log.v("TEST", "origWidth=${origWidth}")
        Log.v("TEST", "origHeight=${origHeight}")

        // Get the actual image dimensions to render
        // Shrink if either width or height is larger than frame
        var imageWidth = origWidth
        var imageHeight = origHeight
        if (origWidth > frameWidth) {
            imageHeight *= (frameWidth / origWidth).toFloat()
            imageWidth = frameWidth
        } else if (origHeight > frameHeight) {
            imageWidth *= (frameHeight / origHeight).toFloat()
            imageHeight = frameHeight
        }

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.henderi01)
        binding.image.setImageDrawable(drawable)
        //binding.image.setImageResource(R.drawable.henderi01)
        val params = binding.image.layoutParams as FrameLayout.LayoutParams
        params.width = imageWidth.toInt()
        params.height = imageHeight.toInt()
        params.leftMargin = ((frameWidth - imageWidth) / 2.0).toInt()
        params.topMargin = ((frameHeight - imageHeight) / 2.0).toInt()
    }
}