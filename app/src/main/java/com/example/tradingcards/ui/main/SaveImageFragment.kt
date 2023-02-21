package com.example.tradingcards.ui.main

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.databinding.FragmentSaveImageBinding
import com.example.tradingcards.viewmodels.SaveImageViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.io.path.Path
import kotlin.io.path.createSymbolicLinkPointingTo


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
        viewModel.currentDirectory = arguments?.getString("currentDirectory") ?: ""

        // For testing
        viewModel.link = "https://www.si.com/.image/t_share/MTY4MjYxMDk5MDc5NTQyMDM3/rickey-henderson-getty3jpg.jpg"
        viewModel.width = 776.toFloat()
        viewModel.height = 1200.toFloat()
        // Fire is an HDPI device, and width will be ~511 dp
        // https://stackoverflow.com/questions/2025282/what-is-the-difference-between-px-dip-dp-and-sp

        // Get screen dims
        val screenWidth = mainReceiver.getScreenDims().getValue("width").toFloat()
        val screenHeight = mainReceiver.getScreenDims().getValue("height").toFloat()

        // Get frame dims
        // Log.v("TEST", "width=${binding.frame.width}")
        // Log.v("TEST", "height=${binding.frame.height}")
        // ^ Not reliable, because onDraw() might come later, and because of keyboard
        val frameWidth = (screenWidth - 32).toFloat()
        val frameHeight = (screenHeight - 32 - 100).toFloat()

        // Get original dims
        val origWidth = viewModel.width.toFloat()
        val origHeight = viewModel.height.toFloat()

        // Get image (actual) dims
        var imageWidth = origWidth
        var imageHeight = origHeight
        var shrink: Float
        // the 2/3 factor is arbitrary, to leave some padding
        if (origWidth > frameWidth) {
            shrink = frameWidth / origWidth * (2/3)
            imageHeight *= shrink
            imageWidth = frameWidth
        } else if (origHeight > frameHeight) {
            shrink = frameHeight / origHeight * (2/3)
            imageWidth *= shrink
            imageHeight = frameHeight
        }

        // Set image dims
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.henderi01)
        binding.image.setImageDrawable(drawable)
        //binding.image.setImageResource(R.drawable.henderi01)
        val imageParams = binding.image.layoutParams as FrameLayout.LayoutParams
        imageParams.width = imageWidth.toInt()
        imageParams.height = imageHeight.toInt()
        imageParams.leftMargin = ((frameWidth - imageWidth) / 2.0).toInt()
        imageParams.topMargin = ((frameHeight - imageHeight) / 2.0).toInt()

        // Get cropper dims
        var cropperWidth = screenWidth
        var cropperHeight = screenHeight
        while (cropperWidth > imageWidth || cropperHeight > imageHeight) {
            cropperWidth -= 1
            cropperHeight = cropperWidth * (screenHeight / screenWidth)
        }

        // Set cropper dims
        val cropperParams = binding.cropper.layoutParams as FrameLayout.LayoutParams
        cropperParams.width = cropperWidth.toInt()
        cropperParams.height = cropperHeight.toInt()
        cropperParams.leftMargin = ((frameWidth - cropperWidth) / 2.0).toInt()
        cropperParams.topMargin = ((frameHeight - cropperHeight) / 2.0).toInt()
        binding.cropper.layoutParams = cropperParams
        binding.cropper.setOnTouchListener(onTouchListener)

        // Save the image
        binding.button.setOnClickListener {

            // Crop the orig
            val cropperOrigin = IntArray(2)
            binding.cropper.getLocationOnScreen(cropperOrigin)
            val imageOrigin = IntArray(2)
            binding.image.getLocationOnScreen(imageOrigin)

            val pctLeft = (cropperOrigin[0] - imageOrigin[0]) / imageWidth
            val pctTop = (cropperOrigin[1] - imageOrigin[1]) / imageHeight

            val left = pctLeft * origWidth
            val top = pctTop * origHeight
            val width = (binding.cropper.width / imageWidth) * origWidth
            val height = (binding.cropper.height / imageHeight) * origHeight

            val bitmap = drawable!!.toBitmap(origWidth.toInt(), origHeight.toInt())
            val resized = Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), width.toInt(), height.toInt())

            Log.v("TEST", "Writing to=${requireContext().filesDir.toString() + "/images/${viewModel.id}.jpg"}")
            val file = File(requireContext().filesDir.toString() + "/images/${viewModel.id}.jpg")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdir()
            }
            val fos = FileOutputStream(file)
            resized.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            // Create symlink in set
            val symlink = requireContext().filesDir.toString() + viewModel.currentDirectory + "${viewModel.id}.jpg"
            val sympath = Path(symlink)
            sympath.createSymbolicLinkPointingTo(Path(file.absolutePath))
        }
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