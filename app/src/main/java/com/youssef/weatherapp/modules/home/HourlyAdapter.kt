package com.youssef.weatherapp.modules.home

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
import com.youssef.weatherapp.model.pojo.CurrentWeather
import com.youssef.weatherapp.utils.Constants.Companion.iconURL
import com.youssef.weatherapp.utils.Formatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HourlyAdapter(var weatherList: List<CurrentWeather>, private val formatter: Formatter, private val context: Context): RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    var offset: Long? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewTime: TextView = view.findViewById(R.id.textViewTimeHourlyCell)
        val textViewTemperature: TextView = view.findViewById(R.id.textViewTemperatureHourlyCell)
        val textViewWindSpeed: TextView = view.findViewById(R.id.textViewWindSpeedHourlyCell)
        val imageViewWeatherIcon: ImageView = view.findViewById(R.id.imageViewWeatherIconHourlyCell)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.hourly_weather_cell, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTime.text = formatter.formatTime(weatherList[position].datetime, offset ?: 0)
        holder.textViewTemperature.text = formatter.formatTemperature(weatherList[position].temperature)
        holder.textViewWindSpeed.text = formatter.formatSpeed(weatherList[position].windSpeed)
        val iconURL = iconURL(weatherList[position].weatherCondition[0].icon)
        var request: RequestBuilder<Drawable>
        CoroutineScope(Dispatchers.IO).launch {
            request = Glide.with(holder.imageViewWeatherIcon).load(iconURL)
            withContext(Dispatchers.Main) {
                request.placeholder(R.drawable.ic_broken_image).into(holder.imageViewWeatherIcon)
            }
        }
    }

    override fun getItemCount(): Int  = weatherList.size / 2
}