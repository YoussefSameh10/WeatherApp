package com.youssef.weatherapp.model.datasources.localdatasource

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather

class LocalDataSource(context: Context): LocalDataSourceInterface {

    companion object {
        private var instance: LocalDataSourceInterface? = null
        fun getInstance(context: Context): LocalDataSourceInterface {
            if(instance == null) {
                instance = LocalDataSource(context)
            }
            return instance as LocalDataSourceInterface
        }
    }

    private var weatherDAO: WeatherDAO
    private var locationDAO: LocationDAO
    private var scheduleAlertDAO: ScheduleAlertDAO


    init {
        val db = WeatherDataBase.getInstance(context)
        weatherDAO = db.weatherDAO()
        locationDAO = db.locationDAO()
        scheduleAlertDAO = db.scheduleAlertDAO()
    }

    override fun getWeather(timezone: String): LiveData<Weather> {
        return weatherDAO.getWeather(timezone)
    }

    override fun insertWeather(weather: Weather) {
        Log.i("DELETE", "insertWeather: ")
        deleteWeather(weather.timezone)
        weatherDAO.insertWeather(weather)
    }

    override fun deleteWeather(timezone: String) {
        weatherDAO.deleteWeather(timezone)
    }

    override fun deleteCurrentWeather() {
        Log.i("DELETE", "deleteCurrentWeather: ")
        weatherDAO.deleteCurrentWeather()
    }


    override fun getScheduledAlerts(): LiveData<ScheduledAlert> {
        return scheduleAlertDAO.getScheduledAlerts()
    }

    override fun addScheduledAlert(scheduledAlert: ScheduledAlert) {
        scheduleAlertDAO.addScheduledAlert(scheduledAlert)
    }

    override fun deleteScheduledAlert(scheduledAlert: ScheduledAlert) {
        scheduleAlertDAO.deleteScheduledAlert(scheduledAlert)
    }

    override fun getFavoriteLocations(): LiveData<List<Location>> {
        return locationDAO.getFavoriteLocations()
    }

    override fun getCurrentLocation(): LiveData<Location> {
        return locationDAO.getCurrentLocation()
    }

    override fun addFavoriteLocation(location: Location) {
        locationDAO.deleteDuplicateFavoriteLocation(location.name)
        locationDAO.addFavoriteLocation(location)
    }

    override fun deleteFavoriteLocation(location: Location) {
        locationDAO.deleteFavoriteLocation(location)
        deleteWeather(location.name)
    }

    override fun addCurrentLocation(location: Location) {
        locationDAO.deleteCurrentLocation()
        locationDAO.addCurrentLocation(location)
    }

}