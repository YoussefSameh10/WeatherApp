package com.youssef.weatherapp.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alert")
data class ScheduledAlert(
    @PrimaryKey var startTime: String,
    var endTime: String,
    var alarmTime: String,
    var alarm: Boolean,
): Serializable {
    var location: Location? = null
}
