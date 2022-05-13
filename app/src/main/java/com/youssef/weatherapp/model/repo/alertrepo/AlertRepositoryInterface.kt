package com.youssef.weatherapp.model.repo.alertrepo

import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Weather

interface AlertRepositoryInterface {
    suspend fun getTodaysAlerts(latitude: Double, longitude: Double): Weather?
    fun getScheduledAlerts(): LiveData<ScheduledAlert>
    fun addScheduledAlert(scheduledAlert: ScheduledAlert)
    fun deleteScheduledAlert(scheduledAlert: ScheduledAlert)
}