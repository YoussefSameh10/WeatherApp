package com.youssef.weatherapp.model.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherAlert(
    @SerializedName("sender_name")
    var senderName: String

): Serializable {

    @SerializedName("event")
    var event: String = ""

    @SerializedName("start")
    var startTime: Long = 0

    @SerializedName("end")
    var endTime: Long = 0

    @SerializedName("description")
    var description: String = ""

    @SerializedName("tags")
    var tags: List<String> = emptyList()

    constructor(
        senderName: String,
        event: String,
        startTime: Long,
        endTime: Long,
        description: String,
        tags: List<String>
    ): this(senderName) {
        this.event = event
        this.startTime = startTime
        this.endTime = endTime
        this.description = description
        this.tags = tags
    }
}
