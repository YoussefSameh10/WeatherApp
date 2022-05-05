package com.youssef.weatherapp.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.youssef.weatherapp.R
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.utils.UIHelper
import retrofit2.create

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel

    private var hasJustOpened = true

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        googleMap.setOnMapClickListener {
            val markerOptions = MarkerOptions()
            markerOptions.position(it)
            googleMap.clear()
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(it));
            googleMap.addMarker(markerOptions)
            hasJustOpened = false
            mapViewModel.locationSelected(it.latitude, it.longitude)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val remoteSource = RetrofitHelper.getInstance().create<RemoteDataSourceInterface>()
        val factory = MapViewModelFactory(
            Repository.getInstance(
                requireContext(), LocalDataSource.getInstance(requireContext()), remoteSource
            ),
            this,
            false
        )
        mapViewModel = ViewModelProvider(activity!!, factory)[MapViewModel::class.java]


        mapViewModel.location.observe(viewLifecycleOwner) {
            if(!hasJustOpened) {
                UIHelper.showConfirmationDialog(
                    requireContext(),
                    getString(R.string.are_you_sure),
                    "",
                    positiveAction = {
                        mapViewModel.locationConfirmed(it)
                        this.findNavController().popBackStack()
                    },
                    negativeAction = {}
                )
            }
        }

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}