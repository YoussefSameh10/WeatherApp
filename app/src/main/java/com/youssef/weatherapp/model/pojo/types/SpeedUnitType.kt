package com.youssef.weatherapp.model.pojo.types

enum class SpeedUnitType(val string: String) {
    MPS("mps"),
    MPH("mph");

    companion object {
        fun getByValue(string: String) = SpeedUnitType.values().find { it.string == string }
    }
}