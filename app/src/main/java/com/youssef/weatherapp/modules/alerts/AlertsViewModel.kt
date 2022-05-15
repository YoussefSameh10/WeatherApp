package com.youssef.weatherapp.modules.alerts

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.youssef.weatherapp.R
import com.youssef.weatherapp.workmanager.RequestManager
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.repo.alertrepo.AlertRepositoryInterface
import com.youssef.weatherapp.utils.NetworkConnectivity
import com.youssef.weatherapp.utils.UIHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertsViewModel(
    private val repo: AlertRepositoryInterface,
    private val owner: LifecycleOwner,
    private val context: Context
): ViewModel() {

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
                        _alert.postValue(it)
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

    var showConnectionErrorDialog = {}

    fun handleAddButton(fragment: AlertsFragment) {
        if(NetworkConnectivity.isNetworkAvailable(context)) {
            fragment.findNavController().navigate(R.id.fragment_add_alert)
        }
        else {
            showConnectionErrorDialog()
        }
    }

}