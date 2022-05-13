package com.youssef.weatherapp.model.repo.locationrepo

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.pojo.Location

interface LocationRepositoryInterface {
    fun getFavoriteLocations(): LiveData<List<Location>>
    fun getCurrentLocation(): LiveData<Location>
    fun addFavoriteLocation(location: Location)
    fun deleteFavoriteLocation(location: Location)
    fun addCurrentLocation(location: Location)
    suspend fun getCityName(latitude: Double, longitude: Double): String

}