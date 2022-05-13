package com.youssef.weatherapp.modules.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.locationrepo.LocationRepositoryInterface
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepositoryInterface
import com.youssef.weatherapp.model.repo.weatherrepo.WeatherRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    val weatherRepo: WeatherRepositoryInterface,
    val locationRepo: LocationRepositoryInterface,
    val preferencesRepo: PreferencesRepositoryInterface,
    val owner: LifecycleOwner
) : ViewModel() {

    private var _weather: MutableLiveData<Weather> = MutableLiveData()
    val weather: LiveData<Weather> get() = _weather

    private var _currentLoc: MutableLiveData<Location> = MutableLiveData()


    @SuppressLint("NullSafeMutableLiveData")
    fun getWeather(favoriteLocation: Location?) {
        Log.i("TAG", "getWeather: ")
        if(favoriteLocation == null) {
            getCurrentLocation()
        }
        else {
            Log.i("TAG", "getWeatherrrrrrrrrrrrrrrr: $favoriteLocation")
            _currentLoc.postValue(favoriteLocation)
        }

        _currentLoc.observe(owner) { location ->

            Log.i("TAGGGG", "getWeatherObserve: $location")
            if (location != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    val weather = weatherRepo.getWeather(location)
                    withContext(Dispatchers.Main) {
                        weather.observe(owner) {
                            if (it != null) {
                                if(favoriteLocation == null) {
                                    it.isCurrent = true
                                }
                                _weather.postValue(it)
                                saveWeather(it)
                            }
                        }
                    }
                }
            } else {
                _weather.postValue(null)
            }

        }
    }

    private fun saveWeather(weather: Weather) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "saveWeather: " + weather)
            if(weather.isCurrent) {
                weatherRepo.deleteCurrentWeather()
            }
            weatherRepo.insertWeather(weather)
        }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val location = locationRepo.getCurrentLocation()
            withContext(Dispatchers.Main) {
                location.observe(owner) {
                    _currentLoc.postValue(it)
                }
            }
        }
    }

    fun isLocationSet(): Boolean {
        return preferencesRepo.isCurrentLocationSet()
    }
}