package com.youssef.weatherapp.model.datasources.localdatasource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.youssef.weatherapp.model.pojo.Location

@Dao
interface LocationDAO {
    @Query("SELECT * FROM location")
    fun getFavoriteLocations(): LiveData<List<Location>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFavoriteLocation(location: Location)

    @Delete
    fun deleteFavoriteLocation(location: Location)
}