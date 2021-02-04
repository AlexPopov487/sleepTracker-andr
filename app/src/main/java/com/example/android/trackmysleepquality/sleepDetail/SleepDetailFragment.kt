package com.example.android.trackmysleepquality.sleepDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepDetailBinding

class SleepDetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSleepDetailBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_detail, container, false)

        val application = requireNotNull(this.activity).application

        // data needed to be passed into the viewModelFactory
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val sleepNightKey = SleepDetailFragmentArgs.fromBundle(arguments!!).sleepNightKey

        val viewModelFactory = SleepDetailViewModelFactory(sleepNightKey, dataSource)
        val sleepDetailViewModel = ViewModelProvider(this, viewModelFactory)
                .get(SleepDetailViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.sleepDetailViewModel = sleepDetailViewModel

        binding.lifecycleOwner = this

        sleepDetailViewModel.navigateBackToSleepTracker.observe(viewLifecycleOwner, Observer { doNavigation ->
            if (doNavigation == true) {
                this.findNavController().navigate(SleepDetailFragmentDirections
                        .actionSleepDetailFragmentToSleepTrackerFragment())
                sleepDetailViewModel.doneNavigating()
            }

        })
        return binding.root
    }
}