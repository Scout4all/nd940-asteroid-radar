package me.bigad.asteroidradar.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.bigad.asteroidradar.api.Network
import me.bigad.asteroidradar.api.asDatabaseModel
import me.bigad.asteroidradar.api.asNetworkModel
import me.bigad.asteroidradar.database.AppDatabase
import me.bigad.asteroidradar.database.DatabaseAsteroid
import me.bigad.asteroidradar.database.asDomainModel
import me.bigad.asteroidradar.domain.Asteroid
import me.bigad.asteroidradar.domain.Constants
import me.bigad.asteroidradar.domain.PhotoOfDay
import timber.log.Timber

enum class ApiStatus { LOADING, ERROR, DONE }

class AsteroidRepository(val database: AppDatabase) {

    // Indicate request errors to show loading screen or network error
    private val _status = MutableLiveData(ApiStatus.LOADING)
    val status: LiveData<ApiStatus>
        get() = _status

//load asteroid list form database
    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidsDao.getAllAsteroids()) {
        it.asDomainModel()
    }

//load photo of day and insert it in to database
    val photoOfDay: LiveData<PhotoOfDay> =
        Transformations.map(database.asteroidsDao.getPhotoOfDay()) {
            it?.asDomainModel()
        }

    private var refreshRunTimes = 0
    suspend fun refreshAsteroids() {

        withContext(Dispatchers.IO) {
            //check if database has records to remove loading screen
            if (database.asteroidsDao.testNotEmpty() != null) {
                _status.postValue(ApiStatus.DONE)
            }
            // make network call to get apo data
            try {
                val asteroidApiCall = Network.apiCall.getAsteroids(Constants.TODAY_DATE)
               val netAsteroidListToDb =  asteroidApiCall.asNetworkModel().asDatabaseModel()

                database.asteroidsDao.insert(*netAsteroidListToDb)
                _status.postValue(ApiStatus.DONE)
            } catch (e: Exception) {
                if (e.message == "timeout") {
                    //retry send request if request time out
                    if( refreshRunTimes < 1) {
                        refreshPhotoOfDay()
                    }else{
                        if (_status.value != ApiStatus.DONE) {
                            _status.postValue(ApiStatus.ERROR)
                        }
                    }
                } else {
                    if (_status.value != ApiStatus.DONE) {
                        _status.postValue(ApiStatus.ERROR)
                    }
                }
            }
        }
        refreshRunTimes++
    }


    suspend fun refreshPhotoOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val photoOfDayApiCall = Network.apiCall.getPhotoOfDay()
                database.asteroidsDao.insertPhotoOfDay(photoOfDayApiCall.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e.message)
                if (e.message == "timeout") {
                    refreshAsteroids()
                } else {
                    ApiStatus.ERROR
                }
            }
        }
    }


}
