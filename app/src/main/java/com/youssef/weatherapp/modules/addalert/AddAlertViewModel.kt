package com.youssef.weatherapp.modules.addalert

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.youssef.weatherapp.workmanager.RequestManager
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddAlertViewModel(private val repo: RepositoryInterface, private val owner: LifecycleOwner, private val context: Context): ViewModel() {

    private var _location: MutableLiveData<Location> = MutableLiveData()

    private var _alert: MutableLiveData<ScheduledAlert> = MutableLiveData()
    val alert: LiveData<ScheduledAlert> get() = _alert

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAlert(alert: ScheduledAlert) {
        getCurrentLocation()
        _location.observe(owner) {
            Log.i("TAG", "setAlert: $it")
            if(it != null) {
                alert.location = it
                viewModelScope.launch(Dispatchers.IO) {
                    Log.i("TAG", "setAlert: $alert")
                    repo.addScheduledAlert(alert)
                    RequestManager.addPeriodicRequest(context, alert)
                    _alert.postValue(alert)
                }
            }
        }
    }

    fun getCurrentLocation() {
        Log.i("TAG", "getCurrentLocation: OUUUUUUUUUUUUUUUUUUUT")
        viewModelScope.launch(Dispatchers.IO) {
            val location = repo.getCurrentLocation()
            withContext(Dispatchers.Main) {
                location.observe(owner) {
                    Log.i("TAG", "getCurrentLocation: $it")
                    _location.postValue(it)
                }

            }
        }
    }
}