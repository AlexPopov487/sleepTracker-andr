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

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    private var tonight = MutableLiveData<SleepNight?>()

    val nights = database.getAllNights()

    // enable the start button IF tonight value is empty
    val startButtonVisible = Transformations.map(tonight){
        it == null}
    // enable the stop button when there is tonight
    val stopButtonVisible = Transformations.map(tonight) {
        it != null}
    // enable the stop button when there is data in database
    val clearButtonVisible = Transformations.map(nights) {
        it.isNotEmpty()}


    // we need to reformat nights variable so that we can show it in the UI
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    // if this equals to null we will not navigate.
    private var _navigateToSleepQualityFragment = MutableLiveData<SleepNight>()
    val navigateToSleepQualityFragment: LiveData<SleepNight>
        get() = _navigateToSleepQualityFragment

    fun doneNavigation() {
        _navigateToSleepQualityFragment.value = null
    }


    private var _showSnackBarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackBarEvent

    fun doneShowingSnackBar(){
        _showSnackBarEvent.value = false
    }

    private val _navigateToSleepDetail = MutableLiveData<Long>()
    val navigateToSleepDetail
        get() = _navigateToSleepDetail

    fun onSleepNightClicked(sleepId: Long){
        _navigateToSleepDetail.value = sleepId
    }

    fun onSleepDetailNavigationDone(){
        _navigateToSleepDetail.value = null
    }
    init {
        initializeTonight()
    }


    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {

        return withContext(Dispatchers.IO) {
            var currentNight = database.getTonight()

            // when created, startTime and endTime of the currentNight are the same until we press
            // the stop button updating the endTime value.
            // That said, if startTime and endTime are not equal at this step, the night was not
            // created at all and we assign it to null
            if (currentNight?.startTimeMillis != currentNight?.endTimeMillis) {
                currentNight = null
            }
            currentNight
        }
    }

    fun onStartTracking() {
        viewModelScope.launch {
            val startTheNight: SleepNight = SleepNight()
            insertNight(startTheNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insertNight(newNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(newNight)
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val stopTheNight = tonight.value ?: return@launch
            stopTheNight.endTimeMillis = System.currentTimeMillis()
            updateNight(stopTheNight)
            _navigateToSleepQualityFragment.value = stopTheNight
        }
    }

    private suspend fun updateNight(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clearNightTable()

            //clear the tonight value as well so the operations like update cannot be completed
            tonight.value = null

            // show shackBar
            _showSnackBarEvent.value = true
        }
    }

    private suspend fun clearNightTable() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }




}

