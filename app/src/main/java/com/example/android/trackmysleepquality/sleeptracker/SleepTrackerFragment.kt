/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        // If I get it right, this is just a context of the whole application
        val application = requireNotNull(this.activity).application

        // this is the instance of SleepDAO needed to be passed into the ViewModel factory
        // along with the application
        val sleepDataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // we take the same params in the viewModelFactory as we do in the viewModel (DAO and APPLICATION)
        // because we need to get access to the database right when the viewModel is created
        // and not to instantiate the sleepDao in the viewModel after it is created
        val viewModelFactory = SleepTrackerViewModelFactory(sleepDataSource, application)

        val sleepTrackerViewModel = ViewModelProvider(this, viewModelFactory)
                .get(SleepTrackerViewModel::class.java)

        // connect viewModel declaration from XML with the actual viewModel
        binding.sleepTrackerViewModel = sleepTrackerViewModel
        // this allows us to always get updated live data
        binding.lifecycleOwner = this

        // if navigateToSleepQualityFragment variable is empty we will not navigate at all
        // otherwise, we will perform navigation passing along the night id
        sleepTrackerViewModel.navigateToSleepQualityFragment.observe(viewLifecycleOwner, Observer { night ->
            night?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigation()
            }
        })


        // show snackBar when data is wiped
        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer { showSnackBar ->
            if (showSnackBar) {
                Snackbar.make(activity!!.findViewById(android.R.id.content),
                        R.string.cleared_message,
                        Snackbar.LENGTH_SHORT)
                        .show()
                sleepTrackerViewModel.doneShowingSnackBar()
            }
        })


        // create grid layout manager for the recycler view
        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager


        // create our custom adapter object
        val adapter = SleepNightAdapter(SleepNightClickListener { sleepNightId ->
            sleepTrackerViewModel.onSleepNightClicked(sleepNightId)
        })
        // attach our adapter to the recycleView widget in xml
        binding.sleepList.adapter = adapter


        // add info into our data list (created in adapter) if there is something to add
        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                // a default method of ListAdapter that helps to update data if the list has changed
                adapter.submitList(it)
            }
        })

        sleepTrackerViewModel.navigateToSleepDetail.observe(viewLifecycleOwner, Observer {night ->
            night?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailFragment(night))
                sleepTrackerViewModel.onSleepDetailNavigationDone()
            }
        })

        return binding.root
    }
}
