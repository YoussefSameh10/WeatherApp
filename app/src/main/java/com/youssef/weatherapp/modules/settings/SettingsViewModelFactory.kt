package com.youssef.weatherapp.modules.settings

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.model.repo.RepositoryInterface

class SettingsViewModelFactory(private val _irepo: RepositoryInterface, private val _owner: LifecycleOwner, private val _context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            SettingsViewModel(_irepo, _owner, _context) as T
        }
        else {
            throw IllegalArgumentException("No ViewModel called SettingsViewModel accepts these arguments")
        }
    }
}

