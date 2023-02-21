package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.databinding.FragmentSaveImageBinding
import com.example.tradingcards.designviews.DIM
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

        val screenWidth = mainReceiver.getScreenDims().getValue("width").toFloat()
        val screenHeight = mainReceiver.getScreenDims().getValue("height").toFloat()

        // Get frame dims
        // Log.v("TEST", "width=${binding.frame.width}")
        // Log.v("TEST", "height=${binding.frame.height}")
        // ^ Not reliable, because onDraw() might come later, and because of keyboard
        val frameWidth = (screenWidth - 32).toFloat()
        val frameHeight = (screenHeight - 32 - 100).toFloat()
        Log.v("TEST", "frameWidth=${frameWidth}")
        Log.v("TEST", "frameHeight=${frameHeight}")

        // Get image's pixel dims
        val origWidth = Utils.convertPxToDp(requireContext(), viewModel.width.toFloat())
        val origHeight = Utils.convertPxToDp(requireContext(), viewModel.height.toFloat())
        Log.v("TEST", "origWidth=${origWidth}")
        Log.v("TEST", "origHeight=${origHeight}")

        /* Get the actual image dimensions to render
         * Shrink if either width or height is larger than frame
         */
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
        val imageParams = binding.image.layoutParams as FrameLayout.LayoutParams
        imageParams.width = imageWidth.toInt()
        imageParams.height = imageHeight.toInt()
        imageParams.leftMargin = ((frameWidth - imageWidth) / 2.0).toInt()
        imageParams.topMargin = ((frameHeight - imageHeight) / 2.0).toInt()

        /*  Now add green cropper to select the cutout
         *  It will have the aspect ratio of the root view (screenDims)
         *  1) If the orig is smaller than the screen (both width and height),
         *     size the cropper entirely within the image
         *  2) If the orig is larger than the screen (width or height)
         *     make the cropper as large as possible, and shrink by
         *     ratio of image:orig
         */

        // 1)
        var cropperWidth = 0.toFloat()
        var cropperHeight = 0.toFloat()
        if (origWidth < screenWidth && origHeight < screenHeight) {
            cropperWidth = origWidth
            cropperHeight = origWidth * (screenHeight / screenWidth)
        }
        // 2) TODO: Not implemented


        // Display the cropper
        val cropperParams = binding.cropper.layoutParams as FrameLayout.LayoutParams
        cropperParams.width = cropperWidth.toInt()
        cropperParams.height = cropperHeight.toInt()
        cropperParams.leftMargin = ((frameWidth - cropperWidth) / 2.0).toInt()
        cropperParams.topMargin = ((frameHeight - cropperHeight) / 2.0).toInt()
        //Log.v("TEST", "cropper width=${cropperParams.width}")
        //Log.v("TEST", "cropper height=${cropperParams.height}")
        //Log.v("TEST", "cropper leftMargin=${cropperParams.leftMargin}")
        //Log.v("TEST", "cropper topMargin=${cropperParams.topMargin}")
        binding.cropper.layoutParams = cropperParams
        binding.cropper.setOnTouchListener(onTouchListener)

    }
}

val onTouchListener = object : View.OnTouchListener {
    // https://stackoverflow.com/questions/7892853/how-to-use-correct-dragging-of-a-view-on-android/18806475#18806475
    var prevX = 0
    var prevY = 0
    var params: FrameLayout.LayoutParams? = null
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v == null || event == null) { return false }
        params = v.layoutParams as FrameLayout.LayoutParams

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                // Get the diff
                val diffX = event.rawX.toInt() - prevX
                val diffY = event.rawY.toInt() - prevY
                params!!.leftMargin += diffX
                params!!.topMargin += diffY
                prevX = event.rawX.toInt()
                prevY = event.rawY.toInt()
                v.layoutParams = params
                return true
            }
            MotionEvent.ACTION_UP -> {
                return true
            }
            MotionEvent.ACTION_DOWN -> {
                prevX = event.rawX.toInt()
                prevY = event.rawY.toInt()

                // little confused about bottom/right margins and the values??
                params!!.bottomMargin = -2 * v.height
                params!!.rightMargin = -2 * v.width
                v.layoutParams = params
                return true
            }
        }
        return false
    }
}