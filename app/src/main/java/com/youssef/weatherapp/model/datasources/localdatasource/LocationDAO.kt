package com.youssef.weatherapp.model.datasources.localdatasource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.youssef.weatherapp.model.pojo.Location

@Dao
interface LocationDAO {
    @Query("SELECT * FROM location WHERE isCurrent = 0")
    fun getFavoriteLocations(): LiveData<List<Location>>

    @Query("SELECT DISTINCT * FROM location WHERE isCurrent = 1")
    fun getCurrentLocation(): LiveData<Location>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFavoriteLocation(location: Location)

    @Query("DELETE FROM location WHERE isCurrent=0 AND name=:name")
    fun deleteDuplicateFavoriteLocation(name: String)

    @Delete
    fun deleteFavoriteLocation(location: Location)

    @Insert
    fun addCurrentLocation(location: Location)

    @Query("DELETE FROM location WHERE isCurrent=1")
    fun deleteCurrentLocation()
}