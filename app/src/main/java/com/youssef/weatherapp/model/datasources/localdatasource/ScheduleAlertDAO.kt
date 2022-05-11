package com.youssef.weatherapp.model.datasources.localdatasource

import androidx.lifecycle.LiveData
import androidx.room.*
import com.youssef.weatherapp.model.pojo.ScheduledAlert

@Dao
interface ScheduleAlertDAO {
    @Query("SELECT * FROM alert")
    fun getScheduledAlerts(): LiveData<ScheduledAlert>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addScheduledAlert(scheduledAlert: ScheduledAlert)

    @Delete
    fun deleteScheduledAlert(scheduledAlert: ScheduledAlert)
}