package com.youssef.weatherapp.modules.addalert

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.model.repo.alertrepo.AlertRepositoryInterface
import com.youssef.weatherapp.model.repo.locationrepo.LocationRepositoryInterface

class AddAlertViewModelFactory(
    private val _ialertRepo: AlertRepositoryInterface,
    private val _ilocationRepo: LocationRepositoryInterface,
    private val _owner: LifecycleOwner,
    private val _context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AddAlertViewModel::class.java)) {
            AddAlertViewModel(_ialertRepo, _ilocationRepo, _owner, _context) as T
        }
        else {
            throw IllegalArgumentException("No ViewModel called AddAlertViewModel accepts these arguments")
        }
    }
}