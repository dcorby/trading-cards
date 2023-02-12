package com.example.tradingcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tradingcards.databinding.ActivityMainBinding

// TODO: Add a drawer with link visible from top-level fragments
// https://m2.material.io/components/navigation-drawer/android#anatomy
// Should display user name/id, link to sets, show share/shared, recent, and trash
// Labels or Tags should provide a filtering overlay to the search fragment
// Allow user to add labels/tags on creation of set and card

class MainActivity : AppCompatActivity() {

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
}