package com.youssef.weatherapp.utils

import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType

class Constants {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        fun iconURL(icon: String): String {
            return " http://openweathermap.org/img/wn/$icon@2x.png"
        }

        const val SHARED_PREFERENCES_FILE_NAME = "settings"

        const val LANGUAGE = "language"
        const val TEMPERATURE_UNIT = "temperature_unit"
        const val SPEED_UNIT = "speed_unit"

        val DEFAULT_LANGUAGE = LanguageType.EN.toString()
        val DEFAULT_TEMPERATURE_UNIT = TemperatureUnitType.CELSIUS.toString()
        val DEFAULT_SPEED_UNIT = SpeedUnitType.MPS.toString()


        const val GPS_PERMISSION_CODE = 1

        const val UNKNOWN_CITY = "Unknown City"
        const val GMT = "GMT"

    }
}