package com.youssef.weatherapp.model.datasources.localdatasource

import android.content.Context
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

    override fun getWeather(latitude: Double, longitude: Double): LiveData<Weather> {
        return weatherDAO.getWeather(latitude, longitude)
    }

    override fun insertWeather(weather: Weather) {
        weatherDAO.deleteWeather(weather.latitude, weather.longitude)
        weatherDAO.insertWeather(weather)
    }


    override fun getScheduledAlerts(): LiveData<List<ScheduledAlert>> {
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
    }

    override fun addCurrentLocation(location: Location) {
        locationDAO.deleteCurrentLocation()
        locationDAO.addCurrentLocation(location)
    }

}