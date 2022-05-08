package com.youssef.weatherapp.model.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CurrentWeather(
    @SerializedName("dt")
    var datetime: Long

): Serializable {

    @SerializedName("temp")
    var temperature: Double = 0.0

    @SerializedName("pressure")
    var pressure: Int = 0

    @SerializedName("humidity")
    var humidity: Int = 0

    @SerializedName("wind_speed")
    var windSpeed: Double = 0.0

    @SerializedName("clouds")
    var clouds: Int = 0

    @SerializedName("weather")
    var weatherCondition: List<WeatherCondition> = emptyList()

    constructor(datetime: Long,
                temperature: Double,
                pressure: Int,
                humidity: Int,
                windSpeed: Double,
                clouds: Int,
                weatherCondition: List<WeatherCondition>
    ): this(datetime) {
        this.temperature = temperature
        this.pressure = pressure
        this.humidity = humidity
        this.windSpeed = windSpeed
        this.clouds = clouds
        this.weatherCondition = weatherCondition
    }

}
