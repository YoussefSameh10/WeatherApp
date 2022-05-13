package com.youssef.weatherapp.model.repo.preferencesrepo

import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType

interface PreferencesRepositoryInterface {

    fun initLanguage()

    fun getLanguage(): LanguageType
    fun getTemperatureUnit(): TemperatureUnitType
    fun getSpeedUnit(): SpeedUnitType
    fun isCurrentLocationSet(): Boolean

    fun setLanguage(language: LanguageType)
    fun setTemperatureUnit(temperatureUnit: TemperatureUnitType)
    fun setSpeedUnit(speedUnit: SpeedUnitType)
    fun setIsCurrentLocationSet(isCurrentLocationSet: Boolean)
}