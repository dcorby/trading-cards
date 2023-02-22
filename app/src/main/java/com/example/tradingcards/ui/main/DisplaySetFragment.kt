package com.example.tradingcards.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.tradingcards.R
import com.example.tradingcards.databinding.FragmentSetBinding
import com.example.tradingcards.viewmodels.DisplaySetViewModel
import java.io.File

class DisplaySetFragment : Fragment() {

    private var _binding: FragmentSetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DisplaySetViewModel

    private lateinit var cardsPagerAdapter: CardsPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DisplaySetViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_display_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val toolbar = requireActivity().findViewById(R.id.toolbar) as Toolbar
        toolbar.visibility = View.GONE

        Log.v("TEST", "DisplayCardsFragment")

        viewModel.id = arguments?.getString("id") ?: ""
        viewModel.currentDirectory = arguments?.getString("currentDirectory") ?: ""

        // Get a list of ids representing the cards to display
        val ids = mutableListOf<String>()
        if (viewModel.id != "") {
            ids.add(viewModel.id)
        } else {
            Log.v("TEST", "Getting files from=${requireContext().filesDir.toString() + viewModel.currentDirectory}")
            val path = File(requireContext().filesDir.toString() + viewModel.currentDirectory)
            path.listFiles().forEach { file ->
                if (file.extension == "jpg") {
                    Log.v("TEST", "adding id=${file.name}")
                    ids.add(file.name.replace(".jpg", ""))
                }
            }
        }

        cardsPagerAdapter = CardsPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = cardsPagerAdapter
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    inner class CardsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int  = 10

        override fun getItem(i: Int): Fragment {
            val fragment = DisplayCardFragment()
            fragment.arguments = Bundle().apply {
                putInt("cardNum", i + 1)
            }
            return fragment
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "OBJECT ${(position + 1)}"
        }
    }
}

