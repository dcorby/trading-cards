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
import com.example.tradingcards.Utils
import com.example.tradingcards.databinding.FragmentSaveImageBinding
import com.example.tradingcards.viewmodels.SaveImageViewModel
import com.example.tradingcards.views.ResizeView
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

        // Display the image, with its own aspect ratio, to max of 80% of frame width and height
        displayImage()

        // Display the cropper, with the screen's aspect ratio, to max of 80% of image width and height
        displayCropper()

        binding.cropper.setOnTouchListener(onTouchListener)

        // Attach the resize view
        val resizeView = ResizeView(requireContext())
        resizeView.cropperView = binding.cropper
        resizeView.show()
        resizeView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        binding.frame.addView(resizeView)

        // Save the image
        binding.button.setOnClickListener {

            /*
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

             */
        }
    }

    private fun displayImage() {
        val screenDims = mainReceiver.getScreenDims()
        val screenWidth = screenDims.getValue("width")
        val screenHeight = screenDims.getValue("height")

        val imageAspectRatio = viewModel.width / viewModel.height
        val frameWidth = screenWidth - 32 * resources.displayMetrics.density
        val frameHeight = screenHeight - 32 * resources.displayMetrics.density - screenDims.getValue("toolbar_height")
        var currentImageWidth = viewModel.width
        var currentImageHeight = viewModel.height

        // Ensure that image width and height are greater than corresponding frame dims
        while (currentImageWidth < frameWidth || currentImageHeight < frameHeight) {
            currentImageWidth += 1
            currentImageHeight = currentImageWidth / imageAspectRatio
        }
        // Now shrink the image down to <=80% of frame width and height
        while (currentImageWidth >= frameWidth * 0.80 || currentImageHeight >= frameHeight * 0.80) {
            currentImageWidth -= 1
            currentImageHeight = currentImageWidth / imageAspectRatio
        }

        // Set image dims
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.henderi01)
        binding.image.setImageDrawable(drawable)
        //binding.image.setImageResource(R.drawable.henderi01)
        val imageParams = binding.image.layoutParams as FrameLayout.LayoutParams
        imageParams.width = currentImageWidth.toInt()
        imageParams.height = currentImageHeight.toInt()
        imageParams.leftMargin = ((frameWidth - currentImageWidth) / 2.0).toInt()
        imageParams.topMargin = ((frameHeight - currentImageHeight) / 2.0).toInt()
    }

    private fun displayCropper() {
        // Get screen dims
        val screenDims = mainReceiver.getScreenDims()
        val screenWidth = screenDims.getValue("width")
        val screenHeight = screenDims.getValue("height")
        val screenAspectRatio = screenWidth / screenHeight
        // Get image dims
        val imageParams = binding.image.layoutParams as FrameLayout.LayoutParams
        val imageWidth = imageParams.width
        val imageHeight = imageParams.height

        // Get cropper dims
        var currentCropperWidth = 1
        var currentCropperHeight = currentCropperWidth / screenAspectRatio

        // Ensure that cropper width and height are greater than corresponding image dims
        while (currentCropperWidth < imageWidth || currentCropperHeight < imageHeight) {
            currentCropperWidth += 1
            currentCropperHeight = currentCropperWidth / screenAspectRatio
        }

        // Now shrink the cropper down to <=80% of image width and height
        while (currentCropperWidth >= imageWidth * 0.80 || currentCropperHeight >= imageHeight * 0.80) {
            currentCropperWidth -= 1
            currentCropperHeight = currentCropperWidth / screenAspectRatio
        }

        // Set cropper dims
        val cropperParams = binding.cropper.layoutParams as FrameLayout.LayoutParams
        cropperParams.width = currentCropperWidth
        cropperParams.height = currentCropperHeight.toInt()
        cropperParams.leftMargin = ((imageWidth - currentCropperWidth) / 2.0).toInt() + imageParams.leftMargin
        cropperParams.topMargin = ((imageHeight - currentCropperHeight) / 2.0).toInt() + imageParams.topMargin
        binding.cropper.layoutParams = cropperParams
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