package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database)

    private val _filter = MutableLiveData<Filter>()
    val filter: LiveData<Filter>
        get() = _filter

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error

    val asteroids = repository.asteroids
    val pictureOfDay = repository.pictureOfDay

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                repository.getImageOfTheDay()
                repository.refreshAsteroids()
            } catch (_: Exception) {
                _error.value = true
            }
        }
    }

    fun finishedShowingError() {
        _error.value = false
    }

    fun updateFilter(filter: Filter) {
        _filter.value = filter
    }

    enum class Filter { WEEK, TODAY, SAVED }
}