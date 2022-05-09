package com.youssef.weatherapp.utils

import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType

class Constants {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        fun iconURL(icon: String): String {
            return "https://openweathermap.org/img/wn/${icon}@2x.png"
        }

        const val SHARED_PREFERENCES_FILE_NAME = "settings"

        const val LANGUAGE = "language"
        const val TEMPERATURE_UNIT = "temperature_unit"
        const val SPEED_UNIT = "speed_unit"
        const val IS_CURRENT_LOCATION_SET = "is_current_location_set"

        val DEFAULT_LANGUAGE = LanguageType.EN.string
        val DEFAULT_TEMPERATURE_UNIT = TemperatureUnitType.CELSIUS.string
        val DEFAULT_SPEED_UNIT = SpeedUnitType.MPS.string
        const val DEFAULT_CURRENT_LOCATION_IS_SET = false


        const val GPS_PERMISSION_CODE = 1

        const val UNKNOWN_CITY = "Unknown City"
        const val GMT = "GMT"

        const val SPEED_CONVERTER = 2.237

        const val IS_CURRENT_LOCATION = "is_current_location"
        const val FAVORITE_LOCATION = "favorite_location"

    }
}