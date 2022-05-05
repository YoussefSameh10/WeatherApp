package com.youssef.weatherapp.model.repo

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.BuildConfig
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.pojo.WeatherAlert
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RepositoryInterface {

    fun initLanguage()

    fun getWeather(latitude: Double, longitude: Double): LiveData<Weather>

    fun getTodaysAlerts(latitude: Double, longitude: Double): List<WeatherAlert>
    suspend fun getCityName(latitude: Double, longitude: Double): String

    fun getScheduledAlerts(): LiveData<List<ScheduledAlert>>
    fun addScheduledAlert(scheduledAlert: ScheduledAlert)
    fun deleteScheduledAlert(scheduledAlert: ScheduledAlert)

    fun getFavoriteLocations(): LiveData<List<Location>>
    fun getCurrentLocation(): LiveData<Location>
    fun addFavoriteLocation(location: Location)
    fun deleteFavoriteLocation(location: Location)
    fun addCurrentLocation(location: Location)

    fun getLanguage(): LanguageType
    fun getTemperatureUnit(): TemperatureUnitType
    fun getSpeedUnit(): SpeedUnitType

    fun setLanguage(language:LanguageType)
    fun setTemperatureUnit(temperatureUnit: TemperatureUnitType)
    fun setSpeedUnit(speedUnit: SpeedUnitType)
}