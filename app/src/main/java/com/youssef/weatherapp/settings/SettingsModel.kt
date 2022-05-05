package com.youssef.weatherapp.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.model.repo.RepositoryInterface

class SettingsModel(var repo: RepositoryInterface) {

    fun getCurrentLocation(): LiveData<Location> {
        return repo.getCurrentLocation()
    }

    fun setCurrentLocation(location: Location) {
        repo.addCurrentLocation(location)
    }

    suspend fun getCityName(latitude: Double, longitude: Double): LiveData<String> {
        val cityNameLive: MutableLiveData<String> = MutableLiveData()
        cityNameLive.postValue(repo.getCityName(latitude, longitude))
        return cityNameLive
    }

    fun getLanguagePreference(): LanguageType {
        return repo.getLanguage()
    }

    fun getTemperatureUnitPreference(): TemperatureUnitType {
        return repo.getTemperatureUnit()
    }

    fun getSpeedUnitPreference(): SpeedUnitType {
        return repo.getSpeedUnit()
    }

    fun setLanguage(language: LanguageType) {
        repo.setLanguage(language)
    }

    fun setTemperatureUnit(temperatureUnit: TemperatureUnitType) {
        repo.setTemperatureUnit(temperatureUnit)
    }

    fun setSpeedUnit(speedUnit: SpeedUnitType) {
        repo.setSpeedUnit(speedUnit)
    }
}