package com.youssef.weatherapp.model.repo.alertrepo

import android.content.Context
import androidx.lifecycle.LiveData
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepository

class AlertRepository private constructor(
    context: Context,
    private var localSource: LocalDataSourceInterface,
    private var remoteSource: RemoteDataSourceInterface
): AlertRepositoryInterface {

    companion object {
        private var instance: AlertRepositoryInterface? = null

        fun getInstance(
            context: Context,
            localSource: LocalDataSourceInterface,
            remoteSource: RemoteDataSourceInterface
        ): AlertRepositoryInterface {
            if(instance == null) {
                instance = AlertRepository(context, localSource, remoteSource)
            }
            return instance as AlertRepositoryInterface
        }
    }

    private val preferencesRepo = PreferencesRepository.getInstance(context)

    override suspend fun getTodaysAlerts(latitude: Double, longitude: Double): Weather? {
        val response = remoteSource.getTodaysAlerts(latitude, longitude, preferencesRepo.getLanguage().string, preferencesRepo.getTemperatureUnit().string)
        return if(response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    override fun getScheduledAlerts(): LiveData<ScheduledAlert> {
        return localSource.getScheduledAlerts()
    }

    override fun addScheduledAlert(scheduledAlert: ScheduledAlert) {
        localSource.addScheduledAlert(scheduledAlert)
    }

    override fun deleteScheduledAlert(scheduledAlert: ScheduledAlert) {
        localSource.deleteScheduledAlert(scheduledAlert)
    }
}