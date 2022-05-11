package com.youssef.weatherapp.view.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(val repo: RepositoryInterface, val owner: LifecycleOwner) : ViewModel() {

    private val homeModel = HomeModel(repo)

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
                    val weather = homeModel.getWeather(location.latitude, location.longitude)
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
                homeModel.deletePreviousWeather()
            }
            homeModel.saveCurrentWeather(weather)
        }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val location = homeModel.getCurrentLocation()
            withContext(Dispatchers.Main) {
                location.observe(owner) {
                    _currentLoc.postValue(it)
                }
            }
        }
    }

    private fun getFavoriteLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val location = homeModel.getCurrentLocation()
            withContext(Dispatchers.Main) {
                location.observe(owner) {
                    _currentLoc.postValue(it)
                }
            }
        }
    }

    fun isLocationSet(): Boolean {
        return homeModel.isLocationSet()
    }
}