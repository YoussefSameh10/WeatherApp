package com.youssef.weatherapp.modules.settings

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherapp.MainActivity
import com.youssef.weatherapp.R
import com.youssef.weatherapp.databinding.FragmentSettingsBinding
import com.youssef.weatherapp.modules.map.MapViewModel
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.pojo.types.LanguageType
import com.youssef.weatherapp.model.pojo.types.SpeedUnitType
import com.youssef.weatherapp.model.pojo.types.TemperatureUnitType
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.model.repo.locationrepo.LocationRepository
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepository
import com.youssef.weatherapp.utils.Constants.Companion.UNKNOWN_CITY
import com.youssef.weatherapp.utils.Formatter
import com.youssef.weatherapp.utils.NetworkConnectivity
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
        Log.i("TAGAGAG", "onDestroyView: ")
        settingsViewModel.currentLoc.removeObservers(viewLifecycleOwner)
        Log.i("TAG", "onDestroyView: " + settingsViewModel.currentLoc.hasObservers())
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        viewModelStore.clear()
    }

    private fun setupFormatter() {
        formatter = Formatter(PreferencesRepository.getInstance(requireContext()))
    }

    private fun setupViewModel() {
        val remoteSource = RetrofitHelper.getInstance().create<RemoteDataSourceInterface>()
        val settingsViewModelFactory = SettingsViewModelFactory(
            LocationRepository.getInstance(
                requireContext(), LocalDataSource.getInstance(requireContext()), remoteSource
            ),
            PreferencesRepository.getInstance(
                requireContext()
            ),
            this,
            requireContext()
        )
        settingsViewModel =
            ViewModelProvider(this, settingsViewModelFactory)[SettingsViewModel::class.java]

        Log.i("TAGAGAG", "setupViewModel: ")


        mapViewModel = ViewModelProvider(activity!!)[MapViewModel::class.java]

    }

    private fun setupView() {

        showProgressDialog()
        showActionBar()

        setLanguageRadioButton()
        setTemperatureUnitRadioButton()
        setSpeedUnitRadioButton()

        handleViewInteractions()
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle(getString(R.string.loading))
        progressDialog.setCancelable(false)
    }

    private fun showActionBar() {
        (requireActivity() as MainActivity).supportActionBar?.show()
    }

    private val setLanguageRadioButton = {
        when (settingsViewModel.getLanguagePreference()) {
            LanguageType.EN -> {
                binding!!.radioButtonEnglish.isChecked = true
            }
            else -> {
                binding!!.radioButtonArabic.isChecked = true
            }
        }
    }

    private val setTemperatureUnitRadioButton = {
        when (settingsViewModel.getTemperatureUnitPreference()) {
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
    }

    private val setSpeedUnitRadioButton = {
        when (settingsViewModel.getSpeedUnitPreference()) {
            SpeedUnitType.MPS -> {
                binding!!.radioButtonMPS.isChecked = true
            }
            else -> {
                binding!!.radioButtonMPH.isChecked = true
            }
        }
    }

    private fun handleViewInteractions() {
        handleLanguageChange()
        handleTemperatureUnitChange()
        handleSpeedUnitChange()
        handleLocationMethodChange()
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

    private fun handleTemperatureUnitChange() {
        binding!!.radioButtonCelsius.setOnClickListener {
            handleTemperatureRadioButton(TemperatureUnitType.CELSIUS)
        }
        binding!!.radioButtonFahrenheit.setOnClickListener {
            handleTemperatureRadioButton(TemperatureUnitType.FAHRENHEIT)
        }
        binding!!.radioButtonKelvin.setOnClickListener {
            handleTemperatureRadioButton(TemperatureUnitType.KELVIN)
        }
    }

    private fun handleTemperatureRadioButton(temperatureUnit: TemperatureUnitType) {
        settingsViewModel.setTemperatureUnitRadioButton = setTemperatureUnitRadioButton
        settingsViewModel.showChangePreferenceAlertDialog = showChangePreferenceAlert
        settingsViewModel.handleTemperatureRadioButtons(temperatureUnit)
    }

    private fun handleSpeedUnitChange() {
        binding!!.radioButtonMPS.setOnClickListener {
            handleSpeedRadioButton(SpeedUnitType.MPS)
        }
        binding!!.radioButtonMPH.setOnClickListener {
            handleSpeedRadioButton(SpeedUnitType.MPH)
        }
    }

    private fun handleSpeedRadioButton(speedUnit: SpeedUnitType) {
        settingsViewModel.setSpeedUnitRadioButton = setSpeedUnitRadioButton
        settingsViewModel.showChangePreferenceAlertDialog = showChangePreferenceAlert
        settingsViewModel.handleSpeedRadioButtons(speedUnit)
    }

    private fun handleLocationMethodChange() {
        binding!!.textViewGPS.setOnClickListener {
            handleLocationOption(isGPS = true)
        }

        binding!!.textViewMap.setOnClickListener {
            handleLocationOption(isGPS = false)
        }

        binding!!.textViewCityName.setOnClickListener {
            handleLocationOption(isGPS = false)
        }

        listenToLocationChange()
        listenToMapLocationChange()
    }

    private fun handleLocationOption(isGPS: Boolean) {
        settingsViewModel.showChangeLocationAlertDialog = showChangeLocationAlert
        settingsViewModel.showProgressDialog = { progressDialog.show() }
        settingsViewModel.handleLocationMethodChange(requireActivity(), this, isGPS)
    }

    private fun listenToLocationChange() {
        Log.i("TAG", "listenToLocationChangeOUT: " + settingsViewModel.currentLoc.hasObservers())

        settingsViewModel.currentLoc.removeObservers(viewLifecycleOwner)
        settingsViewModel.currentLoc.observe(viewLifecycleOwner) {
            Log.i("TAG", "listenToLocationChange: 1")
            it.peekContent()?.let {
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
            }
        }
    }

    private fun listenToMapLocationChange() {
        Log.i("TAGs", "listenToMapLocationChangeOUT: " + mapViewModel.finalLocation)
        mapViewModel.finalLocation.observe(viewLifecycleOwner) {
            Log.i("TAG", "listenToMapLocationChange: 1")
            it.getContentIfNotHandled()?.let {
                Log.i("TAG", "listenToMapLocationChange: ")
                progressDialog.show()
                settingsViewModel.getCityName(it.latitude, it.longitude)
            }
        }
    }

    private fun informNewLocation(location: Location) {
        location.name = formatter.formatCityName(location.name)
        val message = if (location.name == UNKNOWN_CITY) {
            getString(R.string.location_set_confirmation_message) + location.latitude + ", " + location.longitude
        } else {
            getString(R.string.location_set_confirmation_message) + location.name
        }
        UIHelper.showAlertDialog(requireContext(), title = location.name, message = message)
    }

    private val showChangePreferenceAlert = {
        UIHelper.showAlertDialog(
            requireContext(),
            getString(R.string.no_connection),
            getString(R.string.no_connection_change_preferences_message)
        )
    }

    private val showChangeLocationAlert = {
        UIHelper.showAlertDialog(
            requireContext(),
            getString(R.string.no_connection),
            getString(R.string.no_connection_message)
        )
    }
}