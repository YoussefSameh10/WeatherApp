package com.youssef.weatherapp.view.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.youssef.weatherapp.R
import com.youssef.weatherapp.databinding.FragmentHomeBinding
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.pojo.Weather
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.utils.Constants
import com.youssef.weatherapp.utils.Constants.Companion.iconURL
import com.youssef.weatherapp.utils.Formatter
import com.youssef.weatherapp.utils.UIHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog

    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var dailyRecyclerView: RecyclerView

    private lateinit var formatter: Formatter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("TAG", "onCreateView: ")


        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupFormatter()
        setupViewModel()
        initRecyclerViews()
        listenForWeatherChange()

        return binding.root
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupFormatter() {
        val remoteSource = RetrofitHelper.getInstance().create<RemoteDataSourceInterface>()
        formatter = Formatter(
            Repository.getInstance(
                requireContext(), LocalDataSource.getInstance(requireContext()), remoteSource
            )
        )
    }

    private fun setupViewModel() {
        val remoteSource = RetrofitHelper.getInstance().create<RemoteDataSourceInterface>()
        val homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                requireContext(), LocalDataSource.getInstance(requireContext()), remoteSource
            ),
            this
        )
        homeViewModel =
            ViewModelProvider(activity!!, homeViewModelFactory)[HomeViewModel::class.java]

    }

    private fun initRecyclerViews() {
        initHourlyRecyclerView()
        initDailyRecyclerView()
    }

    private fun initHourlyRecyclerView() {
        hourlyRecyclerView = binding.recyclerViewHourly
        val hourlyLayoutManager = LinearLayoutManager(requireContext())
        hourlyLayoutManager.orientation = RecyclerView.HORIZONTAL
        hourlyRecyclerView.layoutManager = hourlyLayoutManager
        hourlyRecyclerView.adapter = HourlyAdapter(emptyList(), formatter, requireContext())
    }

    private fun initDailyRecyclerView() {
        dailyRecyclerView = binding.recyclerViewDaily
        val dailyLayoutManager = LinearLayoutManager(requireContext())
        dailyLayoutManager.orientation = RecyclerView.VERTICAL
        dailyRecyclerView.layoutManager = dailyLayoutManager
        dailyRecyclerView.adapter = DailyAdapter(emptyList(), formatter, requireContext())
    }

    private fun listenForWeatherChange() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.show()
        homeViewModel.getWeather()
        homeViewModel.weather.observe(viewLifecycleOwner) {
            Log.i("TAG", "listenForWeatherChange: $it  /  ${homeViewModel.isLocationSet()}")
            if(!homeViewModel.isLocationSet()) {
                UIHelper.showAlertDialog(
                    requireContext(),
                    getString(R.string.no_location_dialog_title), getString(
                        R.string.no_location_dialog_message
                    )
                )
                findNavController().navigate(R.id.nav_settings)
            }
            else if(it != null) {
                setupView(it)
            }
            progressDialog.dismiss()
        }
    }

    @SuppressLint("NewApi")
    private fun setupView(weather: Weather) {

        refreshHourlyRecyclerView(weather)
        refreshDailyRecyclerView(weather)

        binding.textViewCityName.text = formatter.formatCityName(weather.timezone)
        binding.textViewDate.text = formatter.formatDateWithWeekDay(weather.currentWeather.datetime, weather.timezoneOffset)
        binding.textViewTime.text = formatter.formatTime(weather.currentWeather.datetime, weather.timezoneOffset)
        binding.textViewTemperature.text = formatter.formatTemperature(weather.currentWeather.temperature)
        binding.textViewDescription.text = weather.currentWeather.weatherCondition[0].description


        var request: RequestBuilder<Drawable>
        CoroutineScope(Dispatchers.IO).launch {
            val iconURL = iconURL(weather.currentWeather.weatherCondition[0].icon)
            request = Glide.with(this@HomeFragment).load(iconURL).dontAnimate()
            withContext(Dispatchers.Main) {
                request.placeholder(R.drawable.ic_broken_image).into(binding.imageViewWeatherIcon)
            }
        }

        binding.textViewWindSpeed.text = formatter.formatSpeed(weather.currentWeather.windSpeed)
        binding.textViewPressure.text = formatter.formatPressure(weather.currentWeather.pressure)
        binding.textViewHumidity.text = formatter.formatHumidity(weather.currentWeather.humidity)
        binding.textViewClouds.text = weather.currentWeather.clouds.toString()
    }

    private fun refreshHourlyRecyclerView(weather: Weather) {
        (hourlyRecyclerView.adapter as HourlyAdapter).weatherList = weather.hourlyWeather
        (hourlyRecyclerView.adapter as HourlyAdapter).offset = weather.timezoneOffset
        (hourlyRecyclerView.adapter as HourlyAdapter).notifyDataSetChanged()
    }
    private fun refreshDailyRecyclerView(weather: Weather) {
        (dailyRecyclerView.adapter as DailyAdapter).weatherList = weather.dailyWeather
        (dailyRecyclerView.adapter as DailyAdapter).offset = weather.timezoneOffset
        (dailyRecyclerView.adapter as DailyAdapter).notifyDataSetChanged()
    }
}