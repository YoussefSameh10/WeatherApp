package com.youssef.weatherapp.view.map

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.model.repo.RepositoryInterface

class MapViewModelFactory(private val _irepo: RepositoryInterface, private val _owner: LifecycleOwner, private val _isFavoriteState: Boolean): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(_irepo, _owner, _isFavoriteState) as T
        }
        else {
            throw IllegalArgumentException("No ViewModel called SettingsViewModel accepts these arguments")
        }
    }
}