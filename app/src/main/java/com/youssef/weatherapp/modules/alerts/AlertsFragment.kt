package com.youssef.weatherapp.modules.alerts

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.youssef.weatherapp.R
import com.youssef.weatherapp.databinding.FragmentAlertsBinding
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.model.repo.alertrepo.AlertRepository
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepository
import com.youssef.weatherapp.utils.Formatter
import com.youssef.weatherapp.utils.NetworkConnectivity
import com.youssef.weatherapp.utils.UIHelper
import retrofit2.create


class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var alertsViewModel: AlertsViewModel

    private lateinit var formatter: Formatter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)

        setupViewModel()
        setupFormatter()
        setupView()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        viewModelStore.clear()
    }

    private fun setupViewModel() {
        val factory = AlertViewModelFactory(
            AlertRepository.getInstance(
                requireContext(),
                LocalDataSource.getInstance(requireContext()),
                RetrofitHelper.getInstance().create()
            ),
            this,
            requireContext()
        )

        alertsViewModel = ViewModelProvider(this, factory)[AlertsViewModel::class.java]
    }

    private fun setupFormatter() {
        formatter = Formatter(PreferencesRepository.getInstance(requireContext()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupView() {
        alertsViewModel.location.observe(viewLifecycleOwner) {
            binding.textViewCityName.text = formatter.formatCityName(it.name)
        }

        alertsViewModel.alert.observe(viewLifecycleOwner) {
            binding.textViewTime.text = it.alarmTime
            binding.textViewStartDate.text = formatter.formatDateTimeToDate(it.startTime)
            binding.textViewEndDate.text = formatter.formatDateTimeToDate(it.endTime)
        }
        alertsViewModel.isAlertExist.observe(viewLifecycleOwner) {
            if(it) {
                showViews()

            }
            else {
                hideViews()
            }
        }

        handleDelete()

        handleAdd()
    }

    private fun handleAdd() {
        binding.buttonAdd.setOnClickListener {
            alertsViewModel.showConnectionErrorDialog = showConnectionErrorDialog
            alertsViewModel.handleAddButton(this)
        }
    }


    private fun showViews() {
        Log.i("TAG", "showViews: ")
        binding.cardViewAlert.visibility = View.VISIBLE
        binding.textViewNoAlertsAdded.visibility = View.GONE
        binding.buttonAdd.visibility = View.GONE
    }

    private fun hideViews() {
        binding.cardViewAlert.visibility = View.GONE
        binding.textViewNoAlertsAdded.visibility = View.VISIBLE
        binding.buttonAdd.visibility = View.VISIBLE
    }

    private var showConnectionErrorDialog = {
        UIHelper.showAlertDialog(
            requireContext(),
            getString(R.string.no_connection),
            getString(R.string.no_connection_add_alert_message)
        )
    }

    private fun handleDelete() {
        val confirmDelete = {
            alertsViewModel.deleteAlert()

        }
        binding.imageViewDelete.setOnClickListener {
            UIHelper.showConfirmationDialog(
                requireContext(),
                getString(R.string.remove_alert),
                getString(R.string.remove_alert_confirm_message),
                confirmDelete,
                {}
            )
        }
    }
}