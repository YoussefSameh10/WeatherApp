package com.youssef.weatherapp.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "weather", primaryKeys = ["latitude", "longitude"])
data class Weather(
    @SerializedName("lat")
    var latitude: Double,

    @SerializedName("lon")
    var longitude: Double

): Serializable {
/*
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
*/
    @SerializedName("timezone")
    var timezone: String = ""

    @SerializedName("timezone_offset")
    var timezoneOffset: Long = 0

    @SerializedName("current")
    var currentWeather: CurrentWeather = CurrentWeather(0)

    @SerializedName("hourly")
    var hourlyWeather: List<CurrentWeather> = emptyList()

    @SerializedName("daily")
    var dailyWeather: List<DailyWeather> = emptyList()

    @SerializedName("alerts")
    var weatherAlerts: List<WeatherAlert> = emptyList()

    constructor(
        latitude: Double,
        longitude: Double,
        timezone: String,
        timezoneOffset: Long,
        currentWeather: CurrentWeather,
        hourlyWeather: List<CurrentWeather>,
        dailyWeather: List<DailyWeather>,
        weatherAlerts: List<WeatherAlert>
    ): this(latitude, longitude) {
        this.timezone = timezone
        this.timezoneOffset = timezoneOffset
        this.currentWeather = currentWeather
        this.hourlyWeather = hourlyWeather
        this.dailyWeather = dailyWeather
        this.weatherAlerts = weatherAlerts
    }
}
