package com.youssef.weatherapp.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.youssef.weatherapp.databinding.FragmentSettingsBinding
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.utils.Constants.Companion.GPS_PERMISSION_CODE
import com.youssef.weatherapp.utils.Constants.Companion.UNKNOWN_CITY
import com.youssef.weatherapp.utils.UIHelper
import retrofit2.create

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var binding: FragmentSettingsBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        Log.i("TAG", "onCreateView: ")

        setupViewModel()
        setupView()

        return  binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupViewModel() {
        val remoteSource = RetrofitHelper.getInstance().create<RemoteDataSourceInterface>()
        val factory = SettingsViewModelFactory(
            Repository.getInstance(
                requireContext(), LocalDataSource.getInstance(requireContext()), remoteSource
            ),
        this
        )
        settingsViewModel =
            ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }

    private fun setupView() {
        when(settingsViewModel.getLanguagePreference()) {
            LanguageType.EN -> {
                binding!!.radioButtonEnglish.isChecked = true
            }
            else -> {
                binding!!.radioButtonArabic.isChecked = true
            }
        }

        when(settingsViewModel.getTemperatureUnitPreference()) {
             TemperatureUnitType.CELSIUS -> {
                binding!!.radioButtonCelsius.isChecked = true
            }
            TemperatureUnitType.FAHRENHEIT -> {
                binding!!.radioButtonFahrenheit.isChecked = true
            }
            else -> {
                binding!!.radioButtonKelvin.isChecked = true
            }
        }

        when(settingsViewModel.getSpeedUnitPreference()) {
             SpeedUnitType.MPS -> {
                binding!!.radioButtonMPS.isChecked = true
            }
            else -> {
                binding!!.radioButtonMPH.isChecked = true
            }
        }


        handleViewInteractions()
    }

    private fun handleViewInteractions() {
        handleLanguageChange()
        handleTemperatureUnitChange()
        handleSpeedUnitChange()

        handleLocationMethodChange()

    }

    private fun handleSpeedUnitChange() {
        binding!!.radioButtonMPS.setOnClickListener {
            settingsViewModel.setSpeedUnit(SpeedUnitType.MPS)
        }
        binding!!.radioButtonMPH.setOnClickListener {
            settingsViewModel.setSpeedUnit(SpeedUnitType.MPH)
        }
    }

    private fun handleTemperatureUnitChange() {
        binding!!.radioButtonCelsius.setOnClickListener {
            settingsViewModel.setTemperatureUnit(TemperatureUnitType.CELSIUS)
        }
        binding!!.radioButtonFahrenheit.setOnClickListener {
            settingsViewModel.setTemperatureUnit(TemperatureUnitType.FAHRENHEIT)
        }
        binding!!.radioButtonKelvin.setOnClickListener {
            settingsViewModel.setTemperatureUnit(TemperatureUnitType.KELVIN)
        }
    }

    private fun handleLanguageChange() {
        binding!!.radioButtonEnglish.setOnClickListener {
            settingsViewModel.setLanguage(LanguageType.EN)
        }
        binding!!.radioButtonArabic.setOnClickListener {
            settingsViewModel.setLanguage(LanguageType.AR)
        }
    }

    private fun handleLocationMethodChange() {
        binding!!.radioButtonGPS.setOnClickListener {
            settingsViewModel.handleGPS(activity!!, requireContext())
        }
        listenToLocationChange()
    }

    private fun listenToLocationChange() {
        settingsViewModel.currentLoc.observe(this) {
            Log.i("TAG", "listenToLocationChange: $it")
            var message = ""
            if(it.name == UNKNOWN_CITY) {
                message = "Your location has been set to: " + it.latitude + ", " + it.longitude
            }
            else {
                message = "Your location has been set to: " + it.name
            }
            UIHelper.showAlertDialog(requireContext(), title=it.name, message = message)
        }
    }
}