package com.example.tradingcards

import android.content.ContentValues
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.tradingcards.databinding.ActivityMainBinding
import com.example.tradingcards.db.DBManager
import org.json.JSONArray
import org.json.JSONObject

const val DELETE_SETS_ON_LOAD = false
const val TOOLBAR_HEIGHT = 100

class MainActivity : AppCompatActivity(), MainReceiver {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbManager: DBManager
    private lateinit var screenDims: HashMap<String, Int>

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

        setSupportActionBar(binding.toolbar)
        setTitle("Home") // first screen ignores nav_graph.xml
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestinationId)
        navController.setGraph(navGraph, null)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        if (DELETE_SETS_ON_LOAD) {
            dbManager.exec("DELETE FROM sets", arrayOf())
            filesDir.walkTopDown().forEach { file ->
                if (file != filesDir) {
                    file.deleteRecursively()
                }
            }
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                setScreenDims(TOOLBAR_HEIGHT)
                /* https://stackoverflow.com/questions/41659338/how-to-set-layoutparams-height-width-in-dp-value
                 * "When you specify values programmatically in the LayoutParams, those values are expected to be pixels"
                 */
                binding.toolbar.layoutParams.height = (TOOLBAR_HEIGHT * resources.displayMetrics.density).toInt()
            }
        })
    }

    private fun populateSources() {
        // Return if table is already populated
        val tmp = dbManager.fetch("SELECT * FROM sources", null)
        if (tmp.size > 0) {
            return
        }

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
        return screenDims
    }

    fun setScreenDims(toolbarHeight: Int) {
        if (this::screenDims.isInitialized && screenDims.get("width") != null) {
            return
        }
        screenDims = hashMapOf(
            "width" to binding.root.width,
            "height" to binding.root.height,
            "toolbar_height" to toolbarHeight
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
