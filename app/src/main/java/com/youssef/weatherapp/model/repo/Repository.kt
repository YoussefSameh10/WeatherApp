package com.youssef.weatherapp.model.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.pojo.WeatherAlert
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.utils.NetworkConnectivity
import retrofit2.Response

class Repository private constructor(
    val context: Context,
    var localSource: LocalDataSourceInterface,
    var remoteSource: RemoteDataSourceInterface,
    var language: LanguageType,
    var temperatureUnit: TemperatureUnitType,
): RepositoryInterface {

    companion object {
        private var instance: RepositoryInterface? = null

        fun getInstance(
            context: Context,
            localSource: LocalDataSourceInterface,
            remoteSource: RemoteDataSourceInterface,
            language: LanguageType,
            temperatureUnit: TemperatureUnitType,
        ): RepositoryInterface {
            if(instance == null) {
                instance = Repository(context, localSource, remoteSource, language, temperatureUnit)
            }
            return instance as RepositoryInterface
        }
    }


    private fun getWeatherRemote(latitude: Double, longitude: Double): LiveData<Weather> {
        val response = remoteSource.getWeather(latitude, longitude, temperatureUnit.toString(), language.toString())
        return if(response.isSuccessful) {
            response.body() ?: MutableLiveData()
        } else {
            MutableLiveData()
        }
    }

    private fun getWeatherLocal(latitude: Double, longitude: Double): LiveData<Weather> {
        return localSource.getWeather(latitude, longitude)
    }

    override fun getWeather(latitude: Double, longitude: Double): LiveData<Weather> {
        if(NetworkConnectivity.isNetworkAvailable(context)) {
            return getWeatherRemote(latitude, longitude)
        }
        return getWeatherLocal(latitude, longitude)
    }

    override fun getTodaysAlerts(latitude: Double, longitude: Double): Response<LiveData<List<WeatherAlert>>> {
        return remoteSource.getTodaysAlerts(latitude, longitude, temperatureUnit.toString(), language.toString())
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