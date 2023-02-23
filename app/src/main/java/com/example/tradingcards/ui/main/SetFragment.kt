package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingcards.MainReceiver
import com.example.tradingcards.R
import com.example.tradingcards.Utils
import com.example.tradingcards.adapters.SetAdapter
import com.example.tradingcards.databinding.FragmentSetBinding
import com.example.tradingcards.db.DBManager
import com.example.tradingcards.items.SetItem
import com.example.tradingcards.viewmodels.SetViewModel
import java.io.File

class SetFragment : Fragment() {

    private var _binding: FragmentSetBinding? = null
    private val binding get() = _binding!!
    private lateinit var setAdapter: SetAdapter
    private lateinit var viewModel: SetViewModel
    private lateinit var tracker: SelectionTracker<String>
    private lateinit var mainReceiver: MainReceiver
    private lateinit var dbManager: DBManager
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainReceiver = requireActivity() as MainReceiver
        dbManager = mainReceiver.getDBManager()
        viewModel = ViewModelProvider(this).get(SetViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the currentDirectory, and always display with a trailing slash
        viewModel.currentDirectory = arguments?.getString("currentDirectory", "") ?: ""
        if (viewModel.currentDirectory == "" || viewModel.currentDirectory.last().toString() != "/") {
            viewModel.currentDirectory += "/"
        }

        /*  The root directory is special and cannot contain cards, just other sets
            Thus, it has a null source
         */
        viewModel.source = if (viewModel.currentDirectory == "/") {
            null
        } else {
            dbManager.fetch(
                "SELECT * FROM sets WHERE path = ?",
                arrayOf(viewModel.currentDirectory),
                "source"
            )[0].toString()
        }

        setAdapter = SetAdapter { name -> adapterOnClick(name) }
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = setAdapter

        viewModel.currentSet = File(requireContext().filesDir.toString() + viewModel.currentDirectory)
        setToolbar()

        // Create
        fun create() {
            val bundle = Bundle()
            bundle.putString("currentDirectory", viewModel.currentDirectory)
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_SetFragment_to_CreateSetFragment, bundle)
        }
        binding.create.setOnClickListener { create() }
        binding.addSet.setOnClickListener { create() }

        // Add
        if (viewModel.currentDirectory == "/") {
            // Can't create or view cards in root, because it's not a set
            binding.addCard.visibility = View.GONE
            binding.view.visibility = View.GONE
        } else {
            binding.addCard.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("currentDirectory", viewModel.currentDirectory)
                val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.action_SetFragment_to_SelectPlayerFragment, bundle)
            }
        }

        binding.view.setOnClickListener {
            // Don't use nav controller for this because when you spring from the set, you'll leave the
            // destination as DisplaySetFragment
            //val bundle = Bundle()
            //bundle.putString("currentDirectory", viewModel.currentDirectory)
            //val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            //navController.navigate(R.id.action_SetFragment_to_DisplaySetFragment, bundle)

            viewSet()
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar.visibility = View.VISIBLE
    }

    private fun setToolbar() {
        Log.v("TEST", "setting toolbar")
        val files = viewModel.currentSet.listFiles()
        if (viewModel.currentDirectory != "/" || files.isNotEmpty()) {
            requireActivity().title = ""
            toolbar = requireActivity().findViewById(R.id.toolbar) as Toolbar
            // This won't display if user clicks from SetFragment to SetFragment. Display it manually
            if (parentFragmentManager.backStackEntryCount > 0) {
                toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            }
            toolbar.children.forEach { view ->
                if (view.tag == "title") {
                    toolbar.removeView(view)
                }
            }
            toolbar.addView(Utils.getTitleView(requireContext(), viewModel.currentDirectory, null))
            binding.listParent.visibility = View.VISIBLE

            val setItems = Utils.getSetItems(requireContext(), dbManager, viewModel.source, viewModel.currentSet)
            setAdapter.submitList(setItems)

        } else {
            requireActivity().title = "Get Started"
            binding.gettingStarted.visibility = View.VISIBLE
        }
    }

    private fun viewSet() {
        val bundle = Bundle()
        bundle.putString("currentDirectory", viewModel.currentDirectory)
        val fragment = DisplaySetFragment()
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun adapterOnClick(setItem: SetItem) {
        Log.v("TEST", "onclick")
        val bundle = Bundle()
        if (setItem.file.extension == "jpg") {
            viewSet()
        } else {
            bundle.putString("currentDirectory", viewModel.currentDirectory + setItem.filename)
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.action_SetFragment_to_SetFragment, bundle)
        }
    }
}