package com.youssef.weatherapp.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.utils.Constants
import com.youssef.weatherapp.utils.Serializer
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

object RequestManager {
    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    fun addPeriodicRequest(context: Context, alert: ScheduledAlert) {
        val data = Data.Builder()
            .put(Constants.SCHEDULED_ALERT, Serializer.serializeScheduledAlert(alert))
            .build()

        val addAlertRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            AlertsWorkManager::class.java,
            Duration.of(20, ChronoUnit.MINUTES)
        )
            .setInitialDelay(
                Duration.between(
                    LocalDateTime.now(),
                    LocalDateTime.parse(alert.startTime)
                ).toMillis(), TimeUnit.MILLISECONDS
            )
            .setInputData(data)
            .addTag(Constants.PERIODIC_TIME_REQUEST_TAG)
            .build()


        val worker: WorkManager = WorkManager.getInstance(context)
        worker.enqueue(addAlertRequest)
    }

    fun deleteRequest(context: Context) {
        val worker: WorkManager = WorkManager.getInstance(context)
        worker.cancelAllWorkByTag(Constants.PERIODIC_TIME_REQUEST_TAG)
    }
}