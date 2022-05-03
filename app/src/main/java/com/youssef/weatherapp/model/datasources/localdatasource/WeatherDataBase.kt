package com.youssef.weatherapp.model.datasources.localdatasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.utils.Converters

@Database(entities = [Weather::class, Location::class, ScheduledAlert::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun weatherDAO(): WeatherDAO
    abstract fun locationDAO(): LocationDAO
    abstract fun scheduleAlertDAO(): ScheduleAlertDAO

    companion object {
        private var instance: WeatherDataBase? = null

        fun getInstance(context: Context): WeatherDataBase {
            if(instance == null) {
                instance = Room.databaseBuilder(context, WeatherDataBase::class.java, "weather_app_db").build()
            }
            return instance as WeatherDataBase
        }
    }
}