package com.youssef.weatherapp.modules.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.utils.Constants
import com.youssef.weatherapp.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(val repo: RepositoryInterface, val owner: LifecycleOwner) : ViewModel() {

    var settingsModel: SettingsModel = SettingsModel(repo)

    private var _currentLoc: MutableLiveData<Event<Location?>> = MutableLiveData()
    val currentLoc: LiveData<Event<Location?>> get() = _currentLoc

    lateinit var cityName: String

    init {
        Log.i("TAGAGAG", "Init: ")
        getCurrentLocation()
    }

    override fun onCleared() {
        Log.i("TAGAGAG", "onCleared: ")
    }

    private fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val x = settingsModel.getCurrentLocation()

            withContext(Dispatchers.Main) {
                x.observe(owner) {
                    Log.i("TAGAGAG", "getCurrentLocation: " + it)
                    if(it != null) {
                        _currentLoc.postValue(Event(it))
                    }
                }
            }
        }
    }

    fun getCityName(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val cityNameLive = settingsModel.getCityName(latitude, longitude)
            withContext(Dispatchers.Main) {
                cityNameLive.observe(owner) {
                    cityName = it
                    Log.i("TAG", "getCityName: ")
                    setCurrentLocation(Location(latitude, longitude, cityName, isCurrent = true))
                }
            }
        }
    }

    private fun setCurrentLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAGs", "setCurrentLocation: ")
            //_currentLoc.postValue(location)
            settingsModel.setCurrentLocation(location)
        }
    }

    fun getLanguagePreference(): LanguageType {
        return settingsModel.getLanguagePreference()
    }

    fun getTemperatureUnitPreference(): TemperatureUnitType {
        return settingsModel.getTemperatureUnitPreference()
    }

    fun getSpeedUnitPreference(): SpeedUnitType {
        return settingsModel.getSpeedUnitPreference()
    }

    fun setLanguage(language: LanguageType) {
        settingsModel.setLanguage(language)
    }

    fun setTemperatureUnit(temperatureUnit: TemperatureUnitType) {
        settingsModel.setTemperatureUnit(temperatureUnit)
    }

    fun setSpeedUnit(speedUnit: SpeedUnitType) {
        settingsModel.setSpeedUnit(speedUnit)
    }

    fun setIsLocationSet() {
        settingsModel.setIsLocationSet()
    }

    fun handleGPS(activity: FragmentActivity, context: Context) {
        if(!checkLocationPermitted(context)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.GPS_PERMISSION_CODE
            )
            handleGPS(activity, context)
            return
        }
        val locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        getLocation(activity)

    }

    private fun checkLocationPermitted(context: Context): Boolean {
        return (
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                )
    }

    @SuppressLint("MissingPermission")
    fun getLocation(activity: FragmentActivity) {
        val client = LocationServices.getFusedLocationProviderClient(activity)
        val locationRequest = LocationRequest.create()
        locationRequest.interval =1000
        locationRequest.fastestInterval = 100
        locationRequest.numUpdates = 1
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.i("TAG", "onLocationResult: ${locationResult.lastLocation.latitude}, ${locationResult.lastLocation.longitude}")
                getCityName(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
            }
        }
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
    }

}