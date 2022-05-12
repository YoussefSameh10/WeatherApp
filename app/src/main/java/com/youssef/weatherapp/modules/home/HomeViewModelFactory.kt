package com.youssef.weatherapp.modules.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.model.repo.RepositoryInterface

class HomeViewModelFactory(private val _irepo: RepositoryInterface, private val _owner: LifecycleOwner): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(_irepo, _owner) as T
        }
        else {
            throw IllegalArgumentException("No ViewModel called SettingsViewModel accepts these arguments")
        }
    }
}