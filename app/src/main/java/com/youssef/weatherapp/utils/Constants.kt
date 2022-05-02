package com.youssef.weatherapp.utils

class Constants {
    companion object {
        final val API_KEY = "79191c9df441906d7736bc898474e9c9"
        final val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        fun iconURL(icon: String): String {
            return " http://openweathermap.org/img/wn/$icon@2x.png"
        }
    }
}