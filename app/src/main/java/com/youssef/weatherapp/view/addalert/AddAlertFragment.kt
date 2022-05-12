package com.youssef.weatherapp.view.addalert

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.youssef.weatherapp.R
import com.youssef.weatherapp.databinding.FragmentAddAlertBinding
import com.youssef.weatherapp.databinding.FragmentFavoritesBinding
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.pojo.ScheduledAlert
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.utils.Constants.Companion.UNKNOWN_CITY
import com.youssef.weatherapp.view.alerts.AlertViewModelFactory
import com.youssef.weatherapp.view.alerts.AlertsViewModel
import retrofit2.create
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoField
import java.util.*
import java.util.concurrent.TimeUnit


class AddAlertFragment : Fragment() {

    private var _binding: FragmentAddAlertBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var addAlertViewModel: AddAlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAlertBinding.inflate(inflater, container, false)

        setupViewModel()
        setupView()

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewModel() {
        val factory = AddAlertViewModelFactory(
            Repository.getInstance(
                requireContext(),
                LocalDataSource.getInstance(requireContext()),
                RetrofitHelper.getInstance().create()
            ),
            this,
            requireContext()
        )

        addAlertViewModel = ViewModelProvider(this, factory)[AddAlertViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView() {
        setupTimePickers()
        setupDatePickers()
        handleSubmitButtonVisibility()
        handleSubmit()
    }

    private fun setupTimePickers() {
        binding.timePickerStart.setIs24HourView(true)
        binding.timePickerEnd.setIs24HourView(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDatePickers() {
        binding.datePickerStart.minDate = LocalDateTime.now().getLong(ChronoField.MILLI_OF_SECOND)
        binding.datePickerEnd.minDate =
            LocalDateTime.now().plusDays(1).getLong(ChronoField.MILLI_OF_SECOND)
    }

    private fun handleSubmitButtonVisibility() {
        binding.buttonSubmit.visibility = View.GONE
        if(binding.radioButtonAlarm.isChecked || binding.radioButtonNotification.isChecked) {
            binding.buttonSubmit.visibility = View.VISIBLE
        }

        binding.radioButtonAlarm.setOnClickListener {
            binding.buttonSubmit.visibility = View.VISIBLE
        }

        binding.radioButtonNotification.setOnClickListener {
            binding.buttonSubmit.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSubmit() {
        binding.buttonSubmit.setOnClickListener {
            val startLocalDateTime = LocalDateTime.of(
                LocalDate.of(binding.datePickerStart.year, binding.datePickerStart.month + 1, binding.datePickerStart.dayOfMonth),
                LocalTime.of(binding.timePickerStart.hour, binding.timePickerStart.minute)
            )
            val endLocalDateTime = LocalDateTime.of(
                LocalDate.of(binding.datePickerEnd.year, binding.datePickerEnd.month + 1, binding.datePickerEnd.dayOfMonth),
                LocalTime.of(binding.timePickerEnd.hour, binding.timePickerEnd.minute)
            )

            val isAlarm: Boolean = binding.radioButtonAlarm.isChecked

            addAlertViewModel.setAlert(ScheduledAlert(
                startLocalDateTime.toString(),
                endLocalDateTime.toString(),
                LocalTime.of(binding.timePickerStart.hour, binding.timePickerStart.minute).toString(),
                isAlarm
            ))
            addAlertViewModel.alert.observe(viewLifecycleOwner) {
                findNavController().popBackStack()
            }
        }
    }

}