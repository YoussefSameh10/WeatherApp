package com.youssef.weatherapp.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.databinding.FragmentHomeBinding
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.view.settings.SettingsViewModel
import com.youssef.weatherapp.view.settings.SettingsViewModelFactory
import retrofit2.create

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("TAG", "onCreateView: ")
        setupViewModel()
        setupView()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun setupView() {
        homeViewModel.getWeather()

        listenForWeatherChange()
    }

    private fun listenForWeatherChange() {
        homeViewModel.weather.observe(viewLifecycleOwner) {
            Log.i("TAG", "listenForWeatherChange: ${it.dailyWeather.size}")
        }
    }
}