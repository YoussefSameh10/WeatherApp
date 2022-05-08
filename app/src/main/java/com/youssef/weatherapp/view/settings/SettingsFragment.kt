package com.youssef.weatherapp.view.settings

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.youssef.weatherapp.MainActivity
import com.youssef.weatherapp.R
import com.youssef.weatherapp.databinding.FragmentSettingsBinding
import com.youssef.weatherapp.view.map.MapViewModel
import com.youssef.weatherapp.view.map.MapViewModelFactory
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.utils.Constants.Companion.UNKNOWN_CITY
import com.youssef.weatherapp.utils.Formatter
import com.youssef.weatherapp.utils.UIHelper
import retrofit2.create

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var mapViewModel: MapViewModel
    private var binding: FragmentSettingsBinding? = null

    private lateinit var progressDialog: ProgressDialog

    private lateinit var formatter: Formatter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupFormatter()
        setupViewModel()
        setupView()

        return  binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("TAG", "onDestroyView: ")
        settingsViewModel.currentLoc.removeObservers(viewLifecycleOwner)
        Log.i("TAG", "onDestroyView: " + settingsViewModel.currentLoc.hasObservers())
        binding = null
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
        val settingsViewModelFactory = SettingsViewModelFactory(
            Repository.getInstance(
                requireContext(), LocalDataSource.getInstance(requireContext()), remoteSource
            ),
            this
        )
        settingsViewModel =
            ViewModelProvider(this, settingsViewModelFactory)[SettingsViewModel::class.java]

        val mapViewModelFactory = MapViewModelFactory(
            Repository.getInstance(
                requireContext(), LocalDataSource.getInstance(requireContext()), remoteSource
            ),
            this,
            false
        )
        mapViewModel = ViewModelProvider(activity!!, mapViewModelFactory)[MapViewModel::class.java]

    }

    private fun setupView() {

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle(getString(R.string.loading))
        progressDialog.setCancelable(false)

        (requireActivity() as MainActivity).supportActionBar?.show()

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
            refresh()
        }
        binding!!.radioButtonArabic.setOnClickListener {
            settingsViewModel.setLanguage(LanguageType.AR)
            refresh()
        }
    }

    private fun refresh() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("settings", true)
        startActivity(intent)
        activity!!.finish()
        activity!!.overridePendingTransition(0, 0)
    }

    private fun handleLocationMethodChange() {
        binding!!.textViewGPS.setOnClickListener {
            progressDialog.show()
            settingsViewModel.handleGPS(activity!!, requireContext())
        }

        binding!!.textViewMap.setOnClickListener {
            mapViewModel.navigateFromSettingsToMap(this)
        }

        listenToLocationChange()
        listenToMapLocationChange()
    }

    private fun listenToLocationChange() {
        Log.i("TAG", "listenToLocationChangeOUT: " + settingsViewModel.currentLoc.hasObservers())

        settingsViewModel.currentLoc.removeObservers(viewLifecycleOwner)
        settingsViewModel.currentLoc.observe(viewLifecycleOwner) {
            settingsViewModel.setIsLocationSet()
            Log.i("TAG", "listenToLocationChange: " + it)
            binding!!.textViewCityName.text = formatter.formatCityName(it.name)
            if (it.name == UNKNOWN_CITY) {
                binding!!.textViewCoords.apply {
                    visibility = View.VISIBLE
                    text = "${it.latitude}, ${it.longitude}"
                }
            } else {
                binding!!.textViewCoords.visibility = View.GONE
            }
            progressDialog.dismiss()
            //informNewLocation(it)
        }
    }


    private fun listenToMapLocationChange() {
        Log.i("TAGs", "listenToMapLocationChangeOUT: " + mapViewModel.finalLocation)
        mapViewModel.finalLocation.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                progressDialog.show()
                settingsViewModel.getCityName(it.latitude, it.longitude)
            }
        }
    }

    private fun informNewLocation(location: Location) {
        val message = if (location.name == UNKNOWN_CITY) {
            getString(R.string.location_set_confirmation_message) + location.latitude + ", " + location.longitude
        } else {
            getString(R.string.location_set_confirmation_message) + location.name
        }
        UIHelper.showAlertDialog(requireContext(), title = location.name, message = message)
    }

}