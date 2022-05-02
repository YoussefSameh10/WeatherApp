package com.youssef.weatherapp.model.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherCondition(

    @SerializedName("main")
    var main: String
):Serializable {

    @SerializedName("description")
    var description: String = ""

    @SerializedName("icon")
    var icon: String = ""

    constructor(main: String, description: String, icon: String): this(main) {
        this.description = description
        this.icon = icon
    }
}
