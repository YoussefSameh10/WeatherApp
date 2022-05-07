package com.youssef.weatherapp.view.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(val repo: RepositoryInterface, val owner: LifecycleOwner) : ViewModel() {

    private val homeModel = HomeModel(repo)

    private var _weather: MutableLiveData<Weather> = MutableLiveData()
    val weather: LiveData<Weather> get() = _weather

    private var _currentLoc: MutableLiveData<Location> = MutableLiveData()


    @SuppressLint("NullSafeMutableLiveData")
    fun getWeather() {
        Log.i("TAG", "getWeather: ")
        getCurrentLocation()

        _currentLoc.observe(owner) { location ->
            Log.i("TAG", "getWeatherObserve: $location")
            if(location != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    val weather = homeModel.getWeather(location.latitude, location.longitude)
                    withContext(Dispatchers.Main) {
                        weather.observe(owner) {
                            if(it != null) {
                                _weather.postValue(it)
                                saveWeather(it)
                            }
                        }
                    }
                }
            }
            else {
                _weather.postValue(null)
            }
        }
    }

    private fun saveWeather(weather: Weather) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "saveWeather: " + weather)
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



}