package com.youssef.weatherapp.modules.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.youssef.weatherapp.R
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.utils.Formatter

class FavoriteCitiesAdapter(
    var locations: List<Location>,
    private val formatter: Formatter,
    private val context: Context,
    private val locationClicked: (location: Location) -> Unit,
    private val locationDeleted: (location: Location) -> Unit
): RecyclerView.Adapter<FavoriteCitiesAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewCityName: TextView = view.findViewById(R.id.textViewCityNameFavorite)
        val imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)
        val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraintLayoutFavoriteCityRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.favorite_city_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewCityName.text = formatter.formatCityName(locations[position].name)
        holder.constraintLayout.setOnClickListener() {
            locationClicked(locations[position])
        }
        holder.imageViewDelete.setOnClickListener() {
            locationDeleted(locations[position])
        }
    }

    override fun getItemCount(): Int  = locations.size
}