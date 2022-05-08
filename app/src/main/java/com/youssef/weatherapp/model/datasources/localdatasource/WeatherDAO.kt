package com.youssef.weatherapp.model.datasources.localdatasource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.youssef.weatherapp.model.pojo.Weather

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weather WHERE latitude=:latitude AND longitude=:longitude")
    fun getWeather(latitude: Double, longitude: Double): LiveData<Weather>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWeather(weather: Weather)

    @Query("DELETE FROM weather WHERE latitude=:latitude AND longitude=:longitude")
    fun deleteWeather(latitude: Double, longitude: Double)
}