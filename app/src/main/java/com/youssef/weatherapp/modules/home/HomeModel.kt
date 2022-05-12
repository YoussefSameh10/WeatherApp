package com.youssef.weatherapp.modules.home

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.RepositoryInterface

class HomeModel(val repo: RepositoryInterface) {
    suspend fun getWeather(location: Location): LiveData<Weather> {
        return repo.getWeather(location)
    }

    fun getCurrentLocation(): LiveData<Location> {
        return repo.getCurrentLocation()
    }

    fun deletePreviousWeather() {
        repo.deleteCurrentWeather()
    }

    fun saveCurrentWeather(weather: Weather) {
        repo.insertWeather(weather)
    }

    fun isLocationSet(): Boolean {
        return repo.isCurrentLocationSet()
    }


}