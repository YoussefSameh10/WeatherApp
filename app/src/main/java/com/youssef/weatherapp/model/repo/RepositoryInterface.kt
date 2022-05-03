package com.youssef.weatherapp.model.repo

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.BuildConfig
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.pojo.WeatherAlert
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RepositoryInterface {

    fun getWeather(latitude: Double, longitude: Double, unit: String,
                   language: String, apiKey: String = BuildConfig.WEATHER_API_KEY): Response<LiveData<Weather>>

    fun getTodaysAlerts(latitude: Double, longitude: Double, unit: String,
        language: String, apiKey: String = BuildConfig.WEATHER_API_KEY): Response<LiveData<List<WeatherAlert>>>

    fun getWeather(latitude: Double, longitude: Double): LiveData<Weather>

    fun getScheduledAlerts(): LiveData<List<ScheduledAlert>>
    fun addScheduledAlert(scheduledAlert: ScheduledAlert)
    fun deleteScheduledAlert(scheduledAlert: ScheduledAlert)

    fun getFavoriteLocations(): LiveData<List<Location>>
    fun addFavoriteLocation(location: Location)
    fun deleteFavoriteLocation(location: Location)
}