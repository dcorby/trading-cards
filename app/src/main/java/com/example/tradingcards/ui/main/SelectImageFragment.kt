package com.example.tradingcards.ui.main

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tradingcards.BuildConfig
import com.example.tradingcards.FindAll
import com.example.tradingcards.Sources
import com.example.tradingcards.Utils
import com.example.tradingcards.databinding.FragmentSelectImageBinding
import com.example.tradingcards.viewmodels.SelectImageViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class SelectImageFragment : Fragment() {

    private var _binding: FragmentSelectImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SelectImageViewModel

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

        // Need id and name
        viewModel.id = arguments?.getString("id", "") ?: ""
        viewModel.name = arguments?.getString("name", "") ?: ""

        binding.textView.text = "${viewModel.name} (${viewModel.id})"

        val images = downloadImages()
    }

    data class Image(val link: String, val width: Int, val height: Int, val thumbnailLink: String,
                     val thumbnailWidth: Int, val thumbnailHeight: Int)
    private fun downloadImages() : MutableList<Image> {
        // Live
        //val url = "https://customsearch.googleapis.com/customsearch/v1?imgType=photo&q=${viewModel.name.toLowerCase().replace(" ", "+")}+baseball&searchType=image&cx=${BuildConfig.SEARCH_ENGINE_ID}&key=${BuildConfig.SEARCH_API_KEY}"
        //val response = URL(url)
        //val jsonObject = JSONObject(response.readText())

        // Testing
        val jsonObject = JSONObject(Utils.readAssetsFile(requireContext(), "sample-api-response.json"))

        val images = mutableListOf<Image>()
        val items = Images.toMap(jsonObject).getValue("items") as HashMap<*, *>
        items.keys.forEach {
            val item = id as HashMap<String, *>
            val link = item.getValue("link").toString()
            val img = item.getValue("image") as HashMap<String, String>
            val width = img.getValue("width").toInt()
            val height = img.getValue("height").toInt()
            val thumbnailLink = img.getValue("thumbnailLink").toString()
            val thumbnailWidth = img.getValue("thumbnailWidth").toInt()
            val thumbnailHeight = img.getValue("thumbnailHeight").toInt()
            val image = Image(link, width, height, thumbnailLink, thumbnailWidth, thumbnailHeight)
            images.add(image)
        }
        return images
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