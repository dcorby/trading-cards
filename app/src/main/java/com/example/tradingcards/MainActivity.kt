package com.example.tradingcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.tradingcards.databinding.ActivityMainBinding

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

        val haveSets = false
        val startDestinationId = if (haveSets) {
            R.id.ChooseFragment
        } else {
            R.id.CreateFragment
        }
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestinationId)
        navController.setGraph(navGraph, null)
    }
}