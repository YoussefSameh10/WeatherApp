package com.youssef.weatherapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.utils.Constants.Companion.SPEED_CONVERTER
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Formatter(val repo: RepositoryInterface) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateWithWeekDay(datetime: Long, offset: Long): String {
        val dt = LocalDateTime.ofEpochSecond(datetime, 0, ZoneOffset.ofTotalSeconds(offset.toInt()))
        return dt.toLocalDate().format(DateTimeFormatter.ofPattern("E dd MMM"));
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(datetime: Long, offset: Long): String {
        val dt = LocalDateTime.ofEpochSecond(datetime, 0, ZoneOffset.ofTotalSeconds(offset.toInt()))
        return dt.toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM"));
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTime(datetime: Long, offset: Long): String {
        val dt = LocalDateTime.ofEpochSecond(datetime, 0, ZoneOffset.ofTotalSeconds(offset.toInt()))
        return dt.toLocalTime().format(DateTimeFormatter.ofPattern("kk:mm"));
    }

    fun formatCityName(fullName: String): String {
        var cityName = fullName.substringAfter("/")
        cityName = cityName.replace("_", " ")
        return cityName
    }

    fun formatTemperature(temperature: Double): String {
        val temperatureInt = temperature.toInt()
        return when(repo.getTemperatureUnit()) {
            TemperatureUnitType.CELSIUS -> "${temperatureInt}°C"
            TemperatureUnitType.KELVIN -> "${temperatureInt}K"
            TemperatureUnitType.FAHRENHEIT -> "$temperatureInt°F"
        }
    }

    fun formatSpeed(speed: Double): String {
        when(repo.getTemperatureUnit()) {
            TemperatureUnitType.CELSIUS, TemperatureUnitType.KELVIN -> {
                return when(repo.getSpeedUnit()) {
                    SpeedUnitType.MPS -> {
                        val speedInt = speed.toInt()
                        "$speedInt m/s"
                    }
                    SpeedUnitType.MPH -> {
                        val speedInt = (speed*SPEED_CONVERTER).toInt()
                        "$speedInt m/h"
                    }
                }
            }
            TemperatureUnitType.FAHRENHEIT -> {
                return when(repo.getSpeedUnit()) {
                    SpeedUnitType.MPS -> {
                        val speedInt = (speed/SPEED_CONVERTER).toInt()
                        "$speedInt m/s"
                    }
                    SpeedUnitType.MPH -> {
                        val speedInt = speed.toInt()
                        "$speedInt m/h"
                    }
                }
            }
        }
    }

    fun formatPressure(pressure: Int): String {
        return "$pressure mBar"
    }

    fun formatHumidity(humidity: Int): String {
        return "$humidity%"
    }
}