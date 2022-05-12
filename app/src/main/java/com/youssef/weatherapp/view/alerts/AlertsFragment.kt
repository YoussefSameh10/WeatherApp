package com.youssef.weatherapp.view.alerts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.youssef.weatherapp.R
import com.youssef.weatherapp.databinding.FragmentAlertsBinding
import com.youssef.weatherapp.databinding.FragmentFavoritesBinding
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.utils.Constants.Companion.UNKNOWN_CITY
import com.youssef.weatherapp.utils.Formatter
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

    private fun setupViewModel() {
        val factory = AlertViewModelFactory(
            Repository.getInstance(
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
        formatter = Formatter(
            Repository.getInstance(
                requireContext(),
                LocalDataSource.getInstance(requireContext()),
                RetrofitHelper.getInstance().create()
            )
        )
    }

    private fun setupView() {
        alertsViewModel.location.observe(viewLifecycleOwner) {
            binding.textViewCityName.text = formatter.formatCityName(it.name)
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

        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.fragment_add_alert)
        }
    }

    private fun showViews() {
        Log.i("TAG", "showViews: ")
        binding.textViewCityName.visibility = View.VISIBLE
        binding.imageViewDelete.visibility = View.VISIBLE
        binding.textViewNoAlertsAdded.visibility = View.GONE
        binding.buttonAdd.visibility = View.GONE
    }

    private fun hideViews() {
        binding.textViewCityName.visibility = View.GONE
        binding.imageViewDelete.visibility = View.GONE
        binding.textViewNoAlertsAdded.visibility = View.VISIBLE
        binding.buttonAdd.visibility = View.VISIBLE
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