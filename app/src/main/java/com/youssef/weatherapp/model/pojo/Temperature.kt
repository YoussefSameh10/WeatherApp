package com.youssef.weatherapp.model.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Temperature(

    @SerializedName("day")
    var day: Double,

    @SerializedName("night")
    var night: Double
): Serializable
