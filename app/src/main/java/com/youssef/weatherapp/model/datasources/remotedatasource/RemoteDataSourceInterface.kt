package com.youssef.weatherapp.model.datasources.remotedatasource

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.BuildConfig
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.pojo.WeatherAlert
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteDataSourceInterface {

    @GET("onecall?exclude=minutely,alerts")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unit: String,
        @Query("lang") language: String,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): Response<LiveData<Weather>>

    @GET("onecall?exclude=current,minutely,hourly,daily")
    fun getTodaysAlerts(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unit: String,
        @Query("lang") language: String,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): Response<LiveData<List<WeatherAlert>>>
}