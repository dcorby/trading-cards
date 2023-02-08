package com.example.tradingcards.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tradingcards.R
import com.example.tradingcards.viewmodels.ExampleViewModel

class ExampleFragment : Fragment() {

    // this is just a dummy fragment created by Android Studio

    companion object {
        fun newInstance() = ExampleFragment()
    }

    private lateinit var viewModel: ExampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExampleViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_example, container, false)
    }

}