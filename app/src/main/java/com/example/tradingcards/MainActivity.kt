package com.example.tradingcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
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
        setContentView(R.layout.activity_main)
        //if (savedInstanceState == null) {
        //    supportFragmentManager.beginTransaction()
        //        .replace(R.id.container, MainFragment.newInstance())
        //        .commitNow()
        //}

        // Always land user on SelectSetFragment, and show a message to
        val haveSets = false
        val startDestinationId = if (haveSets) {
            R.id.SelectSetFragment
        } else {
            R.id.SelectSetFragment
        }
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestinationId)
        navController.setGraph(navGraph, null)
    }
}