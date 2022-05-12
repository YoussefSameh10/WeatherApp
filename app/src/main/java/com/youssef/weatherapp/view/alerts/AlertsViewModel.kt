package com.youssef.weatherapp.view.alerts

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.work.*
import com.youssef.weatherapp.AlertsWorkManager
import com.youssef.weatherapp.RequestManager
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.utils.Constants.Companion.PERIODIC_TIME_REQUEST_TAG
import com.youssef.weatherapp.utils.Constants.Companion.SCHEDULED_ALERT
import com.youssef.weatherapp.utils.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertsViewModel(private val repo: RepositoryInterface, private val owner: LifecycleOwner, private val context: Context): ViewModel() {

    private var _alert: MutableLiveData<ScheduledAlert> = MutableLiveData()
    val alert: LiveData<ScheduledAlert> get() = _alert

    var dbAlert: ScheduledAlert? = null

    var isAlertExist: MutableLiveData<Boolean> = MutableLiveData(false)

    private var _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> get() = _location

    init {
        getAlert()
    }

    private fun getAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            val incomingAlerts = repo.getScheduledAlerts()
            withContext(Dispatchers.Main) {
                incomingAlerts.observe(owner) {

                    Log.i("TAG", "getAlert: ${it}")
                    if(it == null) {
                        isAlertExist.postValue(false)
                    }
                    else {
                        Log.i("TAG", "getAlertTrue: ")
                        isAlertExist.postValue(true)
                        _location.postValue(it.location)
                        dbAlert = it
                    }
                }
            }
        }
    }

    fun deleteAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteScheduledAlert(dbAlert!!)
        }
        RequestManager.deleteRequest(context)
    }

}