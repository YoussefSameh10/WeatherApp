package com.youssef.weatherapp.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.youssef.weatherapp.model.pojo.*

class Serializer {
    companion object {
        fun serializeCurrentWeather(currentWeather: CurrentWeather): String {
            return Gson().toJson(currentWeather)
        }

        fun deserializeCurrentWeather(str: String): CurrentWeather {
            return Gson().fromJson(str, CurrentWeather::class.java)
        }

        fun serializeHourlyWeatherList(hourlyWeatherList: List<CurrentWeather>): String {
            return Gson().toJson(hourlyWeatherList)
        }

        fun deserializeHourlyWeatherList(str: String): List<CurrentWeather> {
            val currentWeatherType = object : TypeToken<List<CurrentWeather>>() {}.type
            return Gson().fromJson<List<CurrentWeather>>(str, currentWeatherType)
        }

        fun serializeDailyWeatherList(dailyWeatherList: List<DailyWeather>): String {
            return Gson().toJson(dailyWeatherList)
        }

        fun deserializeDailyWeatherList(str: String): List<DailyWeather> {
            val dailyWeatherType = object : TypeToken<List<DailyWeather>>() {}.type
            return Gson().fromJson<List<DailyWeather>>(str, dailyWeatherType)
        }

        fun serializeTemperature(temperature: Temperature): String {
            return Gson().toJson(temperature)
        }

        fun deserializeTemperature(str: String): Temperature {
            return Gson().fromJson(str, Temperature::class.java)
        }

        fun serializeWeatherAlertList(weatherAlertList: List<WeatherAlert>?): String {
            return Gson().toJson(weatherAlertList)
        }

        fun deserializeWeatherAlertList(str: String): List<WeatherAlert> {
            val weatherAlertType = object : TypeToken<List<WeatherAlert>>() {}.type
            return Gson().fromJson<List<WeatherAlert>>(str, weatherAlertType)
        }

        fun serializeWeatherCondition(weatherCondition: WeatherCondition): String {
            return Gson().toJson(weatherCondition)
        }

        fun deserializeWeatherCondition(str: String): WeatherCondition {
            return Gson().fromJson(str, WeatherCondition::class.java)
        }

        fun serializeLocation(location: Location): String {
            return Gson().toJson(location)
        }

        fun deserializeLocation(str: String): Location {
            return Gson().fromJson(str, Location::class.java)
        }

        fun serializeScheduledAlert(scheduledAlert: ScheduledAlert): String {
            return Gson().toJson(scheduledAlert)
        }

        fun deserializeScheduledAlert(str: String): ScheduledAlert {
            return Gson().fromJson(str, ScheduledAlert::class.java)
        }
    }
}