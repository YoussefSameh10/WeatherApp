package com.youssef.weatherapp.model.repo.weatherrepo

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepository
import com.youssef.weatherapp.utils.NetworkConnectivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository private constructor(
    private val context: Context,
    private var localSource: LocalDataSourceInterface,
    private var remoteSource: RemoteDataSourceInterface
): WeatherRepositoryInterface {

    companion object {
        private var instance: WeatherRepositoryInterface? = null

        fun getInstance(
            context: Context,
            localSource: LocalDataSourceInterface,
            remoteSource: RemoteDataSourceInterface
        ): WeatherRepositoryInterface {
            if(instance == null) {
                instance = WeatherRepository(context, localSource, remoteSource)
            }
            return instance as WeatherRepositoryInterface
        }
    }

    private val preferencesRepo = PreferencesRepository.getInstance(context)

    override suspend fun getWeather(location: Location): LiveData<Weather> {
        if(NetworkConnectivity.isNetworkAvailable(context)) {
            val result = MutableLiveData<Weather>()
            Log.i("TAG", "getWeatherRepo: ")
            val weather = getWeatherRemote(location.latitude, location.longitude)
            withContext(Dispatchers.Main) {
                result.postValue(weather!!)
                Log.i("TAG", "getWeatherRepo: $weather")
            }
            return result
        }
        Log.i("TAG", "getWeatherRepoNoNetwork: ")
        return getWeatherLocal(location.name)
    }

    override fun insertWeather(weather: Weather) {
        localSource.insertWeather(weather)
    }

    override fun deleteWeather(timezone: String) {
        localSource.deleteWeather(timezone)
    }

    override fun deleteCurrentWeather() {
        localSource.deleteCurrentWeather()
    }

    private suspend fun getWeatherRemote(latitude: Double, longitude: Double): Weather? {
        val t: String = preferencesRepo.getTemperatureUnit().string
        Log.i("TAG", "getWeatherRemote: $t")
        val response = remoteSource.getWeather(latitude, longitude, preferencesRepo.getLanguage().string, preferencesRepo.getTemperatureUnit().string)
        Log.i("TAG", "getWeatherRemoteeeeeeeee: ${response.body()!!.currentWeather.temperature}")
        return if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    private fun getWeatherLocal(timezone: String): LiveData<Weather> {
        Log.i("TAG", "getWeatherLocal: $timezone")
        return localSource.getWeather(timezone)
    }

}

