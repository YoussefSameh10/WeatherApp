package com.youssef.weatherapp.model.repo.preferencesrepo

import android.content.Context
import android.util.Log
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.utils.Constants
import java.util.*

class PreferencesRepository private constructor(
    val context: Context,
): PreferencesRepositoryInterface {

    companion object {
        private var instance: PreferencesRepositoryInterface? = null

        fun getInstance(
            context: Context
        ): PreferencesRepositoryInterface {
            if(instance == null) {
                instance = PreferencesRepository(context)
            }
            return instance as PreferencesRepositoryInterface
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

    override fun getLanguage(): LanguageType {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return LanguageType.getByValue(sharedPreferences.getString(
            Constants.LANGUAGE,
            Constants.DEFAULT_LANGUAGE
        )!!)!!
    }

    override fun getTemperatureUnit(): TemperatureUnitType {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return TemperatureUnitType.getByValue(sharedPreferences.getString(
            Constants.TEMPERATURE_UNIT,
            Constants.DEFAULT_TEMPERATURE_UNIT
        )!!)!!
    }

    override fun getSpeedUnit(): SpeedUnitType {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return SpeedUnitType.getByValue(sharedPreferences.getString(
            Constants.SPEED_UNIT,
            Constants.DEFAULT_SPEED_UNIT
        )!!)!!
    }

    override fun isCurrentLocationSet(): Boolean {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(
            Constants.IS_CURRENT_LOCATION_SET,
            Constants.DEFAULT_CURRENT_LOCATION_IS_SET
        )
    }

    override fun setLanguage(language: LanguageType) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(Constants.LANGUAGE, language.string)
            apply()
        }
        this.language = LanguageType.getByValue(sharedPreferences.getString(
            Constants.LANGUAGE,
            Constants.DEFAULT_LANGUAGE
        )!!)!!
        initLanguage()
    }

    override fun setTemperatureUnit(temperatureUnit: TemperatureUnitType) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(Constants.TEMPERATURE_UNIT, temperatureUnit.string)
            apply()
        }
        this.temperatureUnit = TemperatureUnitType.getByValue(sharedPreferences.getString(
            Constants.TEMPERATURE_UNIT,
            Constants.DEFAULT_TEMPERATURE_UNIT
        )!!)!!
    }

    override fun setSpeedUnit(speedUnit: SpeedUnitType) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(Constants.SPEED_UNIT, speedUnit.string)
            apply()
        }
        this.speedUnit = SpeedUnitType.getByValue(sharedPreferences.getString(
            Constants.SPEED_UNIT,
            Constants.DEFAULT_SPEED_UNIT
        )!!)!!
    }

    override fun setIsCurrentLocationSet(isCurrentLocationSet: Boolean) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(Constants.IS_CURRENT_LOCATION_SET, isCurrentLocationSet)
            apply()
        }
        Log.i("TAG", "setIsCurrentLocationSet: ")
        this.isCurrentLocationSet = sharedPreferences.getBoolean(
            Constants.IS_CURRENT_LOCATION_SET,
            Constants.DEFAULT_CURRENT_LOCATION_IS_SET
        )
    }

    private fun initSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        if (!sharedPreferences.contains(Constants.LANGUAGE)) {
            with(sharedPreferences.edit()) {
                putString(Constants.LANGUAGE, Constants.DEFAULT_LANGUAGE)
                putString(Constants.TEMPERATURE_UNIT, Constants.DEFAULT_TEMPERATURE_UNIT)
                putString(Constants.SPEED_UNIT, Constants.DEFAULT_SPEED_UNIT)
                putBoolean(
                    Constants.IS_CURRENT_LOCATION_SET,
                    Constants.DEFAULT_CURRENT_LOCATION_IS_SET
                )
                apply()
            }
        }
        language = LanguageType.getByValue(sharedPreferences.getString(
            Constants.LANGUAGE,
            Constants.DEFAULT_LANGUAGE
        )!!)!!
        temperatureUnit = TemperatureUnitType.getByValue(sharedPreferences.getString(
            Constants.TEMPERATURE_UNIT,
            Constants.DEFAULT_TEMPERATURE_UNIT
        )!!)!!
        speedUnit = SpeedUnitType.getByValue(sharedPreferences.getString(
            Constants.SPEED_UNIT,
            Constants.DEFAULT_SPEED_UNIT
        )!!)!!
        isCurrentLocationSet = sharedPreferences.getBoolean(
            Constants.IS_CURRENT_LOCATION_SET,
            Constants.DEFAULT_CURRENT_LOCATION_IS_SET
        )
    }
}

