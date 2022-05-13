package com.youssef.weatherapp.model.repo.weatherrepo

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather

interface WeatherRepositoryInterface {
    suspend fun getWeather(location: Location): LiveData<Weather>
    fun insertWeather(weather: Weather)
    fun deleteWeather(timezone: String)
    fun deleteCurrentWeather()
}