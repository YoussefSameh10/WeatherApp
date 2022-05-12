package com.youssef.weatherapp.workmanager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.youssef.weatherapp.R
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.utils.Constants.Companion.SCHEDULED_ALERT
import com.youssef.weatherapp.utils.Serializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class AlertsWorkManager(val context: Context, private val workerParams: WorkerParameters): Worker(context, workerParams) {

    private lateinit var repo: RepositoryInterface


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        Log.i("TAG", "doWork: ")

        repo = Repository.getInstance(
            context,
            LocalDataSource.getInstance(context),
            RetrofitHelper.getInstance().create(RemoteDataSourceInterface::class.java)
        )

        val scheduledAlert =
            Serializer.deserializeScheduledAlert(workerParams.inputData.keyValueMap[SCHEDULED_ALERT] as String)


        getAlert(scheduledAlert)

        Log.i("TAG", "doWork: ${LocalDateTime.now().plusDays(1)} ------ ${LocalDateTime.parse(scheduledAlert.endTime)}")
        if(LocalDateTime.now().plusDays(1).isAfter(LocalDateTime.parse(scheduledAlert.endTime))) {
            Log.i("TAG", "DEleTEwoRK")
            repo.deleteScheduledAlert(scheduledAlert)
            RequestManager.deleteRequest(context)
        }

        return Result.success()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAlert(scheduledAlert: ScheduledAlert) {

        CoroutineScope(Dispatchers.IO).launch {
            val result = repo.getTodaysAlerts(scheduledAlert.location?.latitude ?: 0.0, scheduledAlert.location?.longitude ?: 0.0)
            var isThereAlert = true
            var event = ""
            if(result?.weatherAlerts == null) {
                isThereAlert = false
                event = ""
            }
            else {
                event = result.weatherAlerts!![0].event
            }
            withContext(Dispatchers.Main) {

                if (scheduledAlert.alarm) {
                    showAlarm(event, scheduledAlert.location!!.name, isThereAlert)
                } else {
                    showNotification(event, scheduledAlert.location!!.name, isThereAlert)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAlarm(event: String, cityName: String, isThereAlert: Boolean) {
        var title = "Everything is fine today"
        var message = "ENJOY!"
        if(isThereAlert) {
            title = event
            message = "There will be " + event + " in " + cityName
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("default", "default", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "default")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_alert)
            .setDefaults(NotificationCompat.FLAG_INSISTENT)

        notificationManager.notify(1, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(event: String, cityName: String, isThereAlert: Boolean) {
        var title = context.getString(R.string.every_thing_is_fine)
        var message = context.getString(R.string.enjoy)
        if(isThereAlert) {
            title = event
            message = context.getString(R.string.there_will_be) + event + context.getString(R.string.in_word) + cityName
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("default", "default", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "default")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_alert)

        notificationManager.notify(1, notification.build())
    }
}