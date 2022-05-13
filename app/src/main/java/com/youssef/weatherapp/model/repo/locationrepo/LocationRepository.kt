package com.youssef.weatherapp.model.repo.locationrepo

import android.content.Context
import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepository
import com.youssef.weatherapp.utils.Constants

class LocationRepository private constructor(
    context: Context,
    private var localSource: LocalDataSourceInterface,
    private var remoteSource: RemoteDataSourceInterface
): LocationRepositoryInterface {

    companion object {
        private var instance: LocationRepositoryInterface? = null

        fun getInstance(
            context: Context,
            localSource: LocalDataSourceInterface,
            remoteSource: RemoteDataSourceInterface
        ): LocationRepositoryInterface {
            if(instance == null) {
                instance = LocationRepository(context, localSource, remoteSource)
            }
            return instance as LocationRepositoryInterface
        }
    }

    private val preferencesRepo = PreferencesRepository.getInstance(context)

    override suspend fun getCityName(latitude: Double, longitude: Double): String {
        val response = remoteSource.getCityName(latitude, longitude, preferencesRepo.getLanguage().string, preferencesRepo.getTemperatureUnit().string)
        return if(response.isSuccessful) {
            val timezone: String = (response.body() as Weather).timezone
            timezone
        } else {
            Constants.UNKNOWN_CITY
        }
    }

    override fun getFavoriteLocations(): LiveData<List<Location>> {
        return localSource.getFavoriteLocations()
    }

    override fun getCurrentLocation(): LiveData<Location> {
        return localSource.getCurrentLocation()
    }

    override fun addFavoriteLocation(location: Location) {
        localSource.addFavoriteLocation(location)
    }

    override fun deleteFavoriteLocation(location: Location) {
        localSource.deleteFavoriteLocation(location)
    }

    override fun addCurrentLocation(location: Location) {
        localSource.addCurrentLocation(location)
    }
}