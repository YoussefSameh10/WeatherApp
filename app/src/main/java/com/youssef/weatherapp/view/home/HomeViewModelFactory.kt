package com.youssef.weatherapp.view.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.view.settings.SettingsViewModel

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

class SettingsViewModelFactory(private val _irepo: RepositoryInterface, private val _owner: LifecycleOwner): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel(_irepo, _owner) as T
        }
        else {
            throw IllegalArgumentException("No ViewModel called HomeViewModel accepts these arguments")
        }
    }
}