package com.youssef.weatherapp.model.datasources.remotedatasource

import com.youssef.weatherapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private val baseURL = Constants.BASE_URL
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build()
    }

}