package com.youssef.weatherapp.modules.map

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.youssef.weatherapp.R
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.utils.Constants.Companion.UNKNOWN_CITY
import com.youssef.weatherapp.utils.Event

class MapViewModel(): ViewModel() {

    private var _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> get() = _location

    private var _finalLocation: MutableLiveData<Event<Location>> = MutableLiveData()
    val finalLocation: LiveData<Event<Location>> get() = _finalLocation


    fun navigateFromSettingsToMap(fragment: Fragment) {
        fragment.findNavController().navigate(R.id.fragment_map)
    }

    fun locationSelected(latitude: Double, longitude: Double) {
        _location.postValue(Location(latitude, longitude, UNKNOWN_CITY, true))
    }

    fun locationConfirmed(location: Location) {
        Log.i("TAG", "locationConfirmed: ")
        _finalLocation.postValue(Event(location))
    }

}