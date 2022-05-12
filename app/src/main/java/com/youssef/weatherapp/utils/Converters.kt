package com.youssef.weatherapp.utils

import androidx.annotation.Nullable
import androidx.room.TypeConverter
import com.youssef.weatherapp.model.pojo.*

class Converters {
    @TypeConverter
    fun fromCurrentWeather(currentWeather: CurrentWeather): String {
        return Serializer.serializeCurrentWeather(currentWeather)
    }

    @TypeConverter
    fun toCurrentWeather(str: String): CurrentWeather {
        return Serializer.deserializeCurrentWeather(str);
    }

    @TypeConverter
    fun fromCurrentWeatherList(currentWeatherList: List<CurrentWeather>): String {
        return Serializer.serializeHourlyWeatherList(currentWeatherList)
    }

    @TypeConverter
    fun toCurrentWeatherList(str: String): List<CurrentWeather> {
        return Serializer.deserializeHourlyWeatherList(str)
    }

    @TypeConverter
    fun fromDailyWeatherList(dailyWeatherList: List<DailyWeather>): String {
        return Serializer.serializeDailyWeatherList(dailyWeatherList)
    }

    @TypeConverter
    fun toDailyWeatherList(str: String): List<DailyWeather> {
        return Serializer.deserializeDailyWeatherList(str);
    }

    @TypeConverter
    fun fromTemperature(temperature: Temperature): String {
        return Serializer.serializeTemperature(temperature)
    }

    @TypeConverter
    fun toTemperature(str: String): Temperature {
        return Serializer.deserializeTemperature(str)
    }

    @TypeConverter
    fun fromWeatherAlertList(weatherAlertList: List<WeatherAlert>?): String {
        return Serializer.serializeWeatherAlertList(weatherAlertList)
    }

    @TypeConverter
    fun toWeatherAlertList(str: String): List<WeatherAlert> {
        return Serializer.deserializeWeatherAlertList(str)
    }

    @TypeConverter
    fun fromWeatherCondition(weatherCondition: WeatherCondition): String {
        return Serializer.serializeWeatherCondition(weatherCondition)
    }

    @TypeConverter
    fun toWeatherCondition(str: String): WeatherCondition {
        return Serializer.deserializeWeatherCondition(str)
    }

    @TypeConverter
    fun fromLocation(location: Location): String {
        return Serializer.serializeLocation(location)
    }

    @TypeConverter
    fun toLocation(str: String): Location {
        return Serializer.deserializeLocation(str)
    }


}