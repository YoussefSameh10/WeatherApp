package com.youssef.weatherapp.modules.favorites

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.model.repo.locationrepo.LocationRepositoryInterface

class FavoritesViewModelFactory(private val _irepo: LocationRepositoryInterface, private val _owner: LifecycleOwner): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            FavoritesViewModel(_irepo, _owner) as T
        }
        else {
            throw IllegalArgumentException("No ViewModel called FavoritesViewModel accepts these arguments")
        }
    }
}