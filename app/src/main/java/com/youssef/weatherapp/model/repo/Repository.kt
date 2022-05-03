package com.youssef.weatherapp.model.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.pojo.WeatherAlert
import retrofit2.Response

class Repository private constructor(
    var localSource: LocalDataSourceInterface,
    var remoteSource: RemoteDataSourceInterface
): RepositoryInterface {

    companion object {
        private var instance: RepositoryInterface? = null

        fun getInstance(context: Context, localSource: LocalDataSourceInterface, remoteSource: RemoteDataSourceInterface): RepositoryInterface {
            if(instance == null) {
                instance = Repository(localSource, remoteSource)
            }
            return instance as RepositoryInterface
        }
    }

    override fun getWeather(
        latitude: Double,
        longitude: Double,
        unit: String,
        language: String,
        apiKey: String
    ): Response<LiveData<Weather>> {
        return remoteSource.getWeather(latitude, longitude, unit, language)
    }

    override fun getTodaysAlerts(
        latitude: Double,
        longitude: Double,
        unit: String,
        language: String,
        apiKey: String
    ): Response<LiveData<List<WeatherAlert>>> {
        return remoteSource.getTodaysAlerts(latitude, longitude, unit, language)
    }



    override fun getWeather(latitude: Double, longitude: Double): LiveData<Weather> {
        return localSource.getWeather(latitude, longitude)
    }

    override fun getScheduledAlerts(): LiveData<List<ScheduledAlert>> {
        return localSource.getScheduledAlerts()
    }

    override fun addScheduledAlert(scheduledAlert: ScheduledAlert) {
        localSource.addScheduledAlert(scheduledAlert)
    }

    override fun deleteScheduledAlert(scheduledAlert: ScheduledAlert) {
        localSource.deleteScheduledAlert(scheduledAlert)
    }

    override fun getFavoriteLocations(): LiveData<List<Location>> {
        return localSource.getFavoriteLocations()
    }

    override fun addFavoriteLocation(location: Location) {
        localSource.addFavoriteLocation(location)
    }

    override fun deleteFavoriteLocation(location: Location) {
        localSource.deleteFavoriteLocation(location)
    }

}