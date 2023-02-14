package com.example.tradingcards

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.tradingcards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainReceiver {

    private lateinit var binding: ActivityMainBinding
    private var w: Int? = null
    private var h: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val haveSets = false
        val startDestinationId = if (haveSets) {
            R.id.SetFragment
        } else {
            R.id.SetFragment
        }

        binding.root.doOnLayout {
            w = binding.root.width
            h = binding.root.height
            Log.v("TEST", "root width=$w")
            Log.v("TEST", "root height=$h")
        }

        // Required below, to use action bar
        // + supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // + setupActionBarWithNavController(navController)
        // - setSupportActionBar(binding.toolbar)
        // - setTitle("Home")
        // - NavigationUI.setupWithNavController(binding.toolbar, navController)
        // Then in themes.xml
        // - <item name="windowActionBar">false</item>
        // - <item name="windowNoTitle">true</item>

        setSupportActionBar(binding.toolbar)
        setTitle("Home") // first screen ignores nav_graph.xml
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestinationId)
        navController.setGraph(navGraph, null)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        // don't need this
        //val appBarConfiguration = AppBarConfiguration(navGraph)
        //binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    // MainReceiver methods
    override fun getScreenDims() : HashMap<String, Int?> {
        //Log.v("TEST", "binding.parent width=${binding.parent.layoutParams.width}, height=${binding.parent.layoutParams.height}")
        //val displayMetrics = DisplayMetrics()
        //windowManager.defaultDisplay.getMetrics(displayMetrics)
        //val width = displayMetrics.widthPixels
        //val height = displayMetrics.heightPixels
        return hashMapOf(
            "width" to w,
            "height" to h
        )
    }

    override fun getDefaultDesign(w: Int, h: Int) : MutableList<HashMap<String, Any?>> {
        return mutableListOf(
            // Add ShapeView along bottom (solid gray)
            hashMapOf(
                "type" to "ShapeView",
                "width" to w,
                "height" to 100,
                "margin_left" to 0,
                "margin_top" to h - 100,
                "hexadecimal" to "#FFCCCCCC"
            ),
            // Add DataView (player name)
            hashMapOf(
              "type" to "DataView",
              "data" to "name",
              "width" to w - 32 - 100,
              "height" to 50,
              "margin_left" to 50,
              "margin_top" to h - 16 - 50 - 100,
            )
        )
    }
}