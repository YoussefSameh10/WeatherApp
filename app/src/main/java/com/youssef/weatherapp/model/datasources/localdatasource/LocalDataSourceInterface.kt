package com.youssef.weatherapp.model.datasources.localdatasource

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather

interface LocalDataSourceInterface {
    fun getWeather(timezone: String): LiveData<Weather>
    fun insertWeather(weather: Weather)
    fun deleteWeather(timezone: String)
    fun deleteCurrentWeather()

    fun getScheduledAlerts(): LiveData<ScheduledAlert>
    fun addScheduledAlert(scheduledAlert: ScheduledAlert)
    fun deleteScheduledAlert(scheduledAlert: ScheduledAlert)

    fun getFavoriteLocations(): LiveData<List<Location>>
    fun getCurrentLocation(): LiveData<Location>
    fun addFavoriteLocation(location: Location)
    fun deleteFavoriteLocation(location: Location)
    fun addCurrentLocation(location: Location)

}