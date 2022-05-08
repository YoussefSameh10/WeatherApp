package com.youssef.weatherapp.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.youssef.weatherapp.R
import com.youssef.weatherapp.model.pojo.DailyWeather
import com.youssef.weatherapp.utils.Constants.Companion.iconURL
import com.youssef.weatherapp.utils.Formatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DailyAdapter(var weatherList: List<DailyWeather>, private val formatter: Formatter, private val context: Context): RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    var offset: Long? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewDate: TextView = view.findViewById(R.id.textViewDateDailyCell)
        val textViewDescription: TextView = view.findViewById(R.id.textViewDescriptionDailyCell)
        val textViewTemperatureDay: TextView = view.findViewById(R.id.textViewTemperatureDayDailyCell)
        val textViewTemperatureNight: TextView = view.findViewById(R.id.textViewTemperatureNightDailyCell)
        val imageViewWeatherIcon: ImageView = view.findViewById(R.id.imageViewWeatherIconDailyCell)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.daily_weather_cell, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewDate.text = formatter.formatDateWithWeekDay(weatherList[position].datetime, offset ?: 0)
        holder.textViewDescription.text = weatherList[position].weatherCondition[0].description
        holder.textViewTemperatureDay.text = formatter.formatTemperature(weatherList[position].temperature.day)
        holder.textViewTemperatureNight.text = formatter.formatTemperature(weatherList[position].temperature.night)
        val iconURL = iconURL(weatherList[position].weatherCondition[0].icon)
        var request: RequestBuilder<Drawable>
        CoroutineScope(Dispatchers.IO).launch {
            request = Glide.with(holder.imageViewWeatherIcon).load(iconURL)
            withContext(Dispatchers.Main) {
                request.placeholder(R.drawable.ic_broken_image).into(holder.imageViewWeatherIcon)
            }
        }
    }

    override fun getItemCount(): Int  = weatherList.size
}