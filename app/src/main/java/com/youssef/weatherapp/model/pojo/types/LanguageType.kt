package com.youssef.weatherapp.model.pojo.types

enum class LanguageType(val string: String) {
    EN("en"),
    AR("ar");

    companion object {
        fun getByValue(string: String) = LanguageType.values().find { it.string == string }
    }
}