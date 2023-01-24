package me.bigad.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.bigad.asteroidradar.database.getDatabase
import me.bigad.asteroidradar.domain.Asteroid
import me.bigad.asteroidradar.domain.Constants
import me.bigad.asteroidradar.repository.ApiStatus
import me.bigad.asteroidradar.repository.AsteroidRepository
import timber.log.Timber

class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    private val _status = asteroidRepository.status

    //  Indicate request errors to show loading screen or network error
    val status: LiveData<ApiStatus>
        get() = _status

    init {
        viewModelScope.launch {
            //refresh network data
            asteroidRepository.refreshAsteroids()
            asteroidRepository.refreshPhotoOfDay()

        }

    }

    private val _asteroidFullList: MutableLiveData<List<Asteroid>> =
        asteroidRepository.asteroids as MutableLiveData<List<Asteroid>>
    private val _asteroidList: MutableLiveData<List<Asteroid>> =_asteroidFullList
    val asteroidList: LiveData<List<Asteroid>> = _asteroidList


    val photoOfDay = asteroidRepository.photoOfDay


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun searchForToday() {
        _asteroidList.value = Transformations.map(_asteroidList){ asteroid ->
            asteroid.filter {
                Timber.e(it.closeApproachDate + " " + Constants.TODAY_DATE )
               ! it.closeApproachDate.isNullOrBlank()
            }
        }.value
    }
}