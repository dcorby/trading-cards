package com.example.tradingcards

import android.content.ContentValues
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.tradingcards.databinding.ActivityMainBinding
import com.example.tradingcards.db.DBManager
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), MainReceiver {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sources: Map<String, *>
    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbManager = DBManager(this)
        dbManager.open()

        populateSources()

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

    private fun populateSources() {
        // For now, just return if table is already populated
        val tmp = dbManager.fetch("SELECT * FROM sources", null)
        if (tmp.size > 0) { return }

        val jsonObject = JSONObject(Utils.readAssetsFile(this, "sources.json"))
        val sources = Sources.toMap(jsonObject)
        sources.keys.forEach { id ->
            val source = sources[id] as HashMap<*, *>
            val batches = source["batches"] as List<*>
            batches.forEach {
                val batch = it as HashMap<*, *>
                val contentValues = ContentValues()
                contentValues.put("id", id)
                contentValues.put("batch", batch["label"].toString())
                dbManager.insert("sources", contentValues)
            }
        }
    }

    // MainReceiver methods
    override fun getScreenDims() : HashMap<String, Int> {
        // binding.root.doOnLayout {}
        return hashMapOf(
            "width" to binding.root.width,
            "height" to binding.root.height
        )
    }

    override fun getDefaultDesign(width: Int, height: Int) : ArrayList<HashMap<String, Any?>> {
        return arrayListOf(
            // Add ShapeView along bottom (solid gray)
            hashMapOf(
                "type" to "ShapeView",
                "width" to width,
                "height" to 100,
                "margin_left" to 0,
                "margin_top" to height - 100,
                "hexadecimal" to "#FFCCCCCC"
            ),
            // Add DataView (player name)
            hashMapOf(
              "type" to "DataView",
              "data" to "name",
              "width" to width - 50,
              "height" to 100,
              "margin_left" to 50,
              "margin_top" to height - 100
            )
        )
    }

    override fun getDBManager(): DBManager {
        return dbManager
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

class Sources {
    companion object {
        fun toMap(jsonObject : JSONObject) : Map<String, *> {
            return jsonObject.toMap()
        }
    }
}
