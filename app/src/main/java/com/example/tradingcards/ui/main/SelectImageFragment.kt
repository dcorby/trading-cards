package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.adapters.ImageAdapter
import com.example.tradingcards.databinding.FragmentSelectImageBinding
import com.example.tradingcards.items.ImageItem
import com.example.tradingcards.viewmodels.SelectImageViewModel
import org.json.JSONArray
import org.json.JSONObject

class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SelectImageViewModel
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SelectImageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.id = arguments?.getString("id", "") ?: ""
        viewModel.name = arguments?.getString("name", "") ?: ""
        binding.textView.text = "${viewModel.name} (${viewModel.id})"

        imageAdapter = ImageAdapter { imageItem -> adapterOnClick(imageItem) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = imageAdapter

        val images = downloadImages()
        imageAdapter.submitList(images)
    }

    private fun downloadImages() : MutableList<ImageItem> {
        // Live
        //val url = "https://customsearch.googleapis.com/customsearch/v1?imgType=photo&q=${viewModel.name.toLowerCase().replace(" ", "+")}+baseball&searchType=image&cx=${BuildConfig.SEARCH_ENGINE_ID}&key=${BuildConfig.SEARCH_API_KEY}"
        //val response = URL(url)
        //val jsonObject = JSONObject(response.readText())

        // Testing
        val jsonObject = JSONObject(Utils.readAssetsFile(requireContext(), "sample-api-response.json"))

        val images = mutableListOf<ImageItem>()
        val items = Images.toMap(jsonObject).getValue("items") as ArrayList<HashMap<String, *>>
        items.forEach { item ->
            val link = item.getValue("link").toString()
            val img = item.getValue("image") as HashMap<String, *>
            val width = img.getValue("width") as Int
            val height = img.getValue("height") as Int
            val thumbnailLink = img.getValue("thumbnailLink").toString()
            val thumbnailWidth = img.getValue("thumbnailWidth") as Int
            val thumbnailHeight = img.getValue("thumbnailHeight") as Int
            val image = ImageItem(link, width, height, thumbnailLink, thumbnailWidth, thumbnailHeight)
            images.add(image)
        }
        return images
    }

    private fun adapterOnClick(imageItem: ImageItem) {
        val bundle = Bundle()
        bundle.putString("id", viewModel.id)
        bundle.putString("name", viewModel.name)
        bundle.putString("link", imageItem.link)
        bundle.putInt("width", imageItem.width)
        bundle.putInt("height", imageItem.height)

        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.action_SelectImageFragment_to_SaveImageFragment, bundle)
    }
}

// https://stackoverflow.com/questions/44870961/how-to-map-a-json-string-to-kotlin-map
fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it])
    {
        is JSONArray ->
        {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else            -> value
    }
}

class Images {
    companion object {
        fun toMap(jsonObject : JSONObject) : Map<String, *> {
            return jsonObject.toMap()
        }
    }
}