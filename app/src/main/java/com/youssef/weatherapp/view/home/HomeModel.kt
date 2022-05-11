package com.youssef.weatherapp.view.home

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.RepositoryInterface

class HomeModel(val repo: RepositoryInterface) {
    suspend fun getWeather(latitude: Double, longitude: Double): LiveData<Weather> {
        return repo.getWeather(latitude, longitude)
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