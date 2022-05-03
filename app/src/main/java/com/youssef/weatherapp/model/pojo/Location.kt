package com.youssef.weatherapp.model.pojo

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "location", primaryKeys = ["latitude", "longitude"])
data class Location(var latitude: Double, var longitude: Double, var name: String): Serializable
