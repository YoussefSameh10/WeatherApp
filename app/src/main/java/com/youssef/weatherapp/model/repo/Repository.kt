package com.youssef.weatherapp.model.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.pojo.WeatherAlert
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.utils.Constants.Companion.DEFAULT_LANGUAGE
import com.youssef.weatherapp.utils.Constants.Companion.DEFAULT_SPEED_UNIT
import com.youssef.weatherapp.utils.Constants.Companion.DEFAULT_TEMPERATURE_UNIT
import com.youssef.weatherapp.utils.Constants.Companion.GMT
import com.youssef.weatherapp.utils.Constants.Companion.LANGUAGE
import com.youssef.weatherapp.utils.Constants.Companion.SHARED_PREFERENCES_FILE_NAME
import com.youssef.weatherapp.utils.Constants.Companion.TEMPERATURE_UNIT
import com.youssef.weatherapp.utils.Constants.Companion.SPEED_UNIT
import com.youssef.weatherapp.utils.Constants.Companion.UNKNOWN_CITY
import com.youssef.weatherapp.utils.NetworkConnectivity
import com.youssef.weatherapp.utils.Serializer
import retrofit2.Response
import java.util.*
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent

import android.util.DisplayMetrics
import androidx.core.content.ContextCompat


class Repository private constructor(
    val context: Context,
    var localSource: LocalDataSourceInterface,
    var remoteSource: RemoteDataSourceInterface
): RepositoryInterface {

    companion object {
        private var instance: RepositoryInterface? = null

        fun getInstance(
            context: Context,
            localSource: LocalDataSourceInterface,
            remoteSource: RemoteDataSourceInterface
        ): RepositoryInterface {
            if(instance == null) {
                instance = Repository(context, localSource, remoteSource)
            }
            return instance as RepositoryInterface
        }
    }

    private lateinit var language: LanguageType
    private lateinit var temperatureUnit: TemperatureUnitType
    private lateinit var speedUnit: SpeedUnitType

    init {
        initSharedPreferences()
    }

    override fun initLanguage() {
        val config = context.resources.configuration
        val locale = Locale(language.toString())
        Locale.setDefault(locale)
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    override fun getWeather(latitude: Double, longitude: Double): LiveData<Weather> {
        if(NetworkConnectivity.isNetworkAvailable(context)) {
            val weatherLive: MutableLiveData<Weather> = MutableLiveData()
            weatherLive.postValue(getWeatherRemote(latitude, longitude))
            return weatherLive
        }
        return getWeatherLocal(latitude, longitude)
    }

    override fun getTodaysAlerts(latitude: Double, longitude: Double): List<WeatherAlert> {
        val response = remoteSource.getTodaysAlerts(latitude, longitude, temperatureUnit.toString(), language.toString())
        return if(response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun getCityName(latitude: Double, longitude: Double): String {
        val response = remoteSource.getCityName(latitude, longitude, temperatureUnit.toString(), language.toString())
        return if(response.isSuccessful) {
            val timezone: String = (response.body() as Weather).timezone
            var cityName  = timezone.substringAfter('/', UNKNOWN_CITY)
            if(cityName.contains(GMT)) {
                cityName = UNKNOWN_CITY
            }
            cityName
        } else {
            UNKNOWN_CITY
        }
    }

    override fun getScheduledAlerts(): LiveData<List<ScheduledAlert>> {
        return localSource.getScheduledAlerts()
    }

    override fun addScheduledAlert(scheduledAlert: ScheduledAlert) {
        localSource.addScheduledAlert(scheduledAlert)
    }

    override fun deleteScheduledAlert(scheduledAlert: ScheduledAlert) {
        localSource.deleteScheduledAlert(scheduledAlert)
    }

    override fun getFavoriteLocations(): LiveData<List<Location>> {
        return localSource.getFavoriteLocations()
    }

    override fun getCurrentLocation(): LiveData<Location> {
        return localSource.getCurrentLocation()
    }

    override fun addFavoriteLocation(location: Location) {
        localSource.addFavoriteLocation(location)
    }

    override fun deleteFavoriteLocation(location: Location) {
        localSource.deleteFavoriteLocation(location)
    }

    override fun addCurrentLocation(location: Location) {
        localSource.addCurrentLocation(location)
    }

    override fun getLanguage(): LanguageType {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return LanguageType.valueOf(sharedPreferences.getString(LANGUAGE, DEFAULT_LANGUAGE)!!)
    }

    override fun getTemperatureUnit(): TemperatureUnitType {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return TemperatureUnitType.valueOf(sharedPreferences.getString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)!!)
    }

    override fun getSpeedUnit(): SpeedUnitType {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return SpeedUnitType.valueOf(sharedPreferences.getString(SPEED_UNIT, DEFAULT_SPEED_UNIT)!!)
    }

    override fun setLanguage(language: LanguageType) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(LANGUAGE, language.toString())
            apply()
        }
        this.language = LanguageType.valueOf(sharedPreferences.getString(LANGUAGE, DEFAULT_LANGUAGE)!!)
        initLanguage()
    }

    override fun setTemperatureUnit(temperatureUnit: TemperatureUnitType) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(TEMPERATURE_UNIT, temperatureUnit.toString())
            apply()
        }
        this.temperatureUnit = TemperatureUnitType.valueOf(sharedPreferences.getString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)!!)
    }

    override fun setSpeedUnit(speedUnit: SpeedUnitType) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(SPEED_UNIT, speedUnit.toString())
            apply()
        }
        this.speedUnit = SpeedUnitType.valueOf(sharedPreferences.getString(SPEED_UNIT, DEFAULT_SPEED_UNIT)!!)
    }

    private fun initSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        if (!sharedPreferences.contains(LANGUAGE)) {
            with(sharedPreferences.edit()) {
                putString(LANGUAGE, DEFAULT_LANGUAGE)
                putString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)
                putString(SPEED_UNIT, DEFAULT_SPEED_UNIT)
                apply()
            }
        }
        language = LanguageType.valueOf(sharedPreferences.getString(LANGUAGE, DEFAULT_LANGUAGE)!!)
        temperatureUnit = TemperatureUnitType.valueOf(sharedPreferences.getString(TEMPERATURE_UNIT,DEFAULT_TEMPERATURE_UNIT)!!)
        speedUnit =SpeedUnitType.valueOf(sharedPreferences.getString(SPEED_UNIT, DEFAULT_SPEED_UNIT)!!)
    }

    private fun getWeatherRemote(latitude: Double, longitude: Double): Weather? {
        val response = remoteSource.getWeather(latitude, longitude, temperatureUnit.toString(), language.toString())
        return if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    private fun getWeatherLocal(latitude: Double, longitude: Double): LiveData<Weather> {
        return localSource.getWeather(latitude, longitude)
    }


}