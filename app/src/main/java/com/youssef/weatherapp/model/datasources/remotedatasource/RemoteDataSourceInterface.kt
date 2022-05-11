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
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") unit: String,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): Response<Weather>

    @GET("onecall?exclude=current,minutely,hourly,daily")
    suspend fun getTodaysAlerts(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") unit: String,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): Response<Weather>

    @GET("onecall?exclude=current,minutely,hourly,daily,alerts")
    suspend fun getCityName(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") unit: String,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY
    ): Response<Weather>
}