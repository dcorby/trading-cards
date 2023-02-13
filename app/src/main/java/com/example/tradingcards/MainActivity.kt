package com.example.tradingcards

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.tradingcards.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainReceiver {

    private lateinit var binding: ActivityMainBinding

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
    override fun getScreenDims() : HashMap<String, Int> {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        return hashMapOf(
            "width" to width,
            "height" to height
        )
    }
}