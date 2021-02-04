package com.example.android.trackmysleepquality.sleepDetail

import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight


class SleepDetailViewModel(private val sleepNightId: Long = 0L,
                           dataSource: SleepDatabaseDao) : ViewModel() {
    // hold a reference to the dataBase via dbDAO
    val database = dataSource

    // Mediator liveData may observe other LiveData objects and react on OnChanged events from them.
    private val night = MediatorLiveData<SleepNight>()

    fun getNight() = night



    init {
        val nightLiveDataSource = database.getNightWithId(sleepNightId)
        night.addSource(nightLiveDataSource, night::setValue)

    }

    // LiveData event that tells the fragment whether we should navigate to sleepTrackerFragment
    private var _navigateBackToSleepTracker = MutableLiveData<Boolean?>()
    val navigateBackToSleepTracker: LiveData<Boolean?>
        get() = _navigateBackToSleepTracker

    fun onClose() {
        _navigateBackToSleepTracker.value = true
    }

    fun doneNavigating() {
        _navigateBackToSleepTracker.value = null
    }


}