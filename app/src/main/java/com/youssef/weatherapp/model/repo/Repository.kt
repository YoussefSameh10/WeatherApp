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
import android.util.Log
import androidx.core.content.ContextCompat
import com.youssef.weatherapp.utils.Constants.Companion.DEFAULT_CURRENT_LOCATION_IS_SET
import com.youssef.weatherapp.utils.Constants.Companion.IS_CURRENT_LOCATION_SET
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
    private var isCurrentLocationSet: Boolean = false

    init {
        initSharedPreferences()
    }

    override fun initLanguage() {
        val config = context.resources.configuration
        val locale = Locale(language.string)
        Locale.setDefault(locale)
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        Log.i("TAGsGs", "initLanguage: ")
    }

    override suspend fun getWeather(latitude: Double, longitude: Double): LiveData<Weather> {
        Log.i("TAG", "getWeatherRepo: ")
        if(NetworkConnectivity.isNetworkAvailable(context)) {
            val result = MutableLiveData<Weather>()
            Log.i("TAG", "getWeatherRepo: ")
            val weather = getWeatherRemote(latitude, longitude)
            withContext(Dispatchers.Main) {
                result.postValue(weather!!)
                Log.i("TAG", "getWeatherRepo: " + weather)
            }
            return result
        }
        return getWeatherLocal(latitude, longitude)
    }

    override fun insertWeather(weather: Weather) {
        localSource.insertWeather(weather)
    }

    override fun deleteWeather(timezone: String) {
        localSource.deleteWeather(timezone)
    }

    override fun deleteCurrentWeather() {
        localSource.deleteCurrentWeather()
    }


    override suspend fun getTodaysAlerts(latitude: Double, longitude: Double): Weather? {
        val response = remoteSource.getTodaysAlerts(latitude, longitude, language.string, temperatureUnit.string)
        return if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override suspend fun getCityName(latitude: Double, longitude: Double): String {
        val response = remoteSource.getCityName(latitude, longitude, language.string, temperatureUnit.string)
        return if(response.isSuccessful) {
            val timezone: String = (response.body() as Weather).timezone
            timezone
        } else {
            UNKNOWN_CITY
        }
    }

    override fun getScheduledAlerts(): LiveData<ScheduledAlert> {
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
        return LanguageType.getByValue(sharedPreferences.getString(LANGUAGE, DEFAULT_LANGUAGE)!!)!!
    }

    override fun getTemperatureUnit(): TemperatureUnitType {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return TemperatureUnitType.getByValue(sharedPreferences.getString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)!!)!!
    }

    override fun getSpeedUnit(): SpeedUnitType {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return SpeedUnitType.getByValue(sharedPreferences.getString(SPEED_UNIT, DEFAULT_SPEED_UNIT)!!)!!
    }

    override fun isCurrentLocationSet(): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_CURRENT_LOCATION_SET, DEFAULT_CURRENT_LOCATION_IS_SET)
    }

    override fun setLanguage(language: LanguageType) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(LANGUAGE, language.string)
            apply()
        }
        this.language = LanguageType.getByValue(sharedPreferences.getString(LANGUAGE, DEFAULT_LANGUAGE)!!)!!
        initLanguage()
    }

    override fun setTemperatureUnit(temperatureUnit: TemperatureUnitType) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(TEMPERATURE_UNIT, temperatureUnit.string)
            apply()
        }
        this.temperatureUnit = TemperatureUnitType.getByValue(sharedPreferences.getString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)!!)!!
    }

    override fun setSpeedUnit(speedUnit: SpeedUnitType) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(SPEED_UNIT, speedUnit.string)
            apply()
        }
        this.speedUnit = SpeedUnitType.getByValue(sharedPreferences.getString(SPEED_UNIT, DEFAULT_SPEED_UNIT)!!)!!
    }

    override fun setIsCurrentLocationSet(isCurrentLocationSet: Boolean) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(IS_CURRENT_LOCATION_SET, isCurrentLocationSet)
            apply()
        }
        Log.i("TAG", "setIsCurrentLocationSet: ")
        this.isCurrentLocationSet = sharedPreferences.getBoolean(IS_CURRENT_LOCATION_SET, DEFAULT_CURRENT_LOCATION_IS_SET)
    }

    private fun initSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        if (!sharedPreferences.contains(LANGUAGE)) {
            with(sharedPreferences.edit()) {
                putString(LANGUAGE, DEFAULT_LANGUAGE)
                putString(TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)
                putString(SPEED_UNIT, DEFAULT_SPEED_UNIT)
                putBoolean(IS_CURRENT_LOCATION_SET, DEFAULT_CURRENT_LOCATION_IS_SET)
                apply()
            }
        }
        language = LanguageType.getByValue(sharedPreferences.getString(LANGUAGE, DEFAULT_LANGUAGE)!!)!!
        temperatureUnit = TemperatureUnitType.getByValue(sharedPreferences.getString(TEMPERATURE_UNIT,DEFAULT_TEMPERATURE_UNIT)!!)!!
        speedUnit = SpeedUnitType.getByValue(sharedPreferences.getString(SPEED_UNIT, DEFAULT_SPEED_UNIT)!!)!!
        isCurrentLocationSet = sharedPreferences.getBoolean(IS_CURRENT_LOCATION_SET, DEFAULT_CURRENT_LOCATION_IS_SET)
    }

    private suspend fun getWeatherRemote(latitude: Double, longitude: Double): Weather? {
        val t: String = temperatureUnit.string
        Log.i("TAG", "getWeatherRemote: $t")
        val response = remoteSource.getWeather(latitude, longitude, language.string, temperatureUnit.string)
        Log.i("TAG", "getWeatherRemoteeeeeeeee: ${response.body()!!.currentWeather.temperature}")
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