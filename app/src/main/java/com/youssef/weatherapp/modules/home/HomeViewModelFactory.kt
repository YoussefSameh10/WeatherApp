package com.youssef.weatherapp.modules.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.model.repo.locationrepo.LocationRepositoryInterface
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepositoryInterface
import com.youssef.weatherapp.model.repo.weatherrepo.WeatherRepositoryInterface

class HomeViewModelFactory(
    private val _iweatherRepo: WeatherRepositoryInterface,
    private val _ilocationRepo: LocationRepositoryInterface,
    private val _ipreferencesRepo: PreferencesRepositoryInterface,
    private val _owner: LifecycleOwner
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(_iweatherRepo, _ilocationRepo, _ipreferencesRepo, _owner) as T
        }
        else {
            throw IllegalArgumentException("No ViewModel called HomeViewModel accepts these arguments")
        }
    }
}