package com.youssef.weatherapp.model.pojo.types

enum class TemperatureUnitType(val string: String) {
    CELSIUS("metric"),
    KELVIN("standard"),
    FAHRENHEIT("imperial");

    companion object {
        fun getByValue(string: String) = TemperatureUnitType.values().find { it.string == string }
    }

}