package com.youssef.weatherapp.modules.favorites

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
import com.youssef.weatherapp.MainActivity
import com.youssef.weatherapp.R
import com.youssef.weatherapp.databinding.FragmentFavoritesBinding
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSource
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RetrofitHelper
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.repo.Repository
import com.youssef.weatherapp.model.repo.locationrepo.LocationRepository
import com.youssef.weatherapp.model.repo.preferencesrepo.PreferencesRepository
import com.youssef.weatherapp.utils.Constants.Companion.FAVORITE_LOCATION
import com.youssef.weatherapp.utils.Constants.Companion.IS_CURRENT_LOCATION
import com.youssef.weatherapp.utils.Formatter
import com.youssef.weatherapp.modules.map.MapViewModel
import com.youssef.weatherapp.utils.NetworkConnectivity
import com.youssef.weatherapp.utils.UIHelper
import retrofit2.create

class FavoritesFragment : Fragment() {

    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentFavoritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var formatter: Formatter

    private lateinit var locationClicked: (location: Location) -> Unit
    private lateinit var locationDeleted: (location: Location) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

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
        val service = RetrofitHelper.getInstance().create<RemoteDataSourceInterface>()
        val favoritesViewModelFactory = FavoritesViewModelFactory(
            LocationRepository.getInstance(
                requireContext(),
                LocalDataSource.getInstance(requireContext()),
                service
            ),
            this
        )
        favoritesViewModel = ViewModelProvider(this, favoritesViewModelFactory)[FavoritesViewModel::class.java]
        favoritesViewModel.getFavoriteLocations()


        mapViewModel = ViewModelProvider(activity!!)[MapViewModel::class.java]
    }

    private fun setupFormatter() {
        formatter = Formatter(PreferencesRepository.getInstance(requireContext()))
    }

    private fun setupView() {

        (requireActivity() as MainActivity).supportActionBar?.show()

        favoritesViewModel.isFavoriteExist.observe(viewLifecycleOwner) {
            if(it) {
                binding.textViewNoFavorites.visibility = View.GONE
            }
            else {
                binding.textViewNoFavorites.visibility = View.VISIBLE
            }
        }

        binding.buttonAdd.setOnClickListener() {
            favoritesViewModel.showConnectionErrorDialog = showConnectionErrorDialog
            favoritesViewModel.handleAdd(this, requireContext())
        }

        handleLocationClicked()
        handleLocationDeleted()
        initRecyclerView()
        listenForLocationsChange()
        listenForMapLocationAdded()
    }

    private fun handleLocationClicked() {
        locationClicked = {
            val bundle = Bundle()
            bundle.putBoolean(IS_CURRENT_LOCATION, false)
            bundle.putSerializable(FAVORITE_LOCATION, it)
            findNavController().navigate(R.id.nav_home, bundle)
        }
    }

    private fun handleLocationDeleted() {
        locationDeleted = { location ->
            UIHelper.showConfirmationDialog(
                requireContext(),
                "Remove from favorites",
                "Are you sure you want to remove this city from favorites",
                {favoritesViewModel.deleteFavoriteLocation(location)},
                {}
            )

        }
    }

    private fun initRecyclerView() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = FavoriteCitiesAdapter(
            emptyList(),
            formatter,
            requireContext(),
            locationClicked,
            locationDeleted
        )
    }

    private fun listenForLocationsChange() {
        favoritesViewModel.favoriteLocations.observe(viewLifecycleOwner) { locations ->
            refreshRecyclerView(locations)
        }
    }

    private fun refreshRecyclerView(locations: List<Location>) {
        (recyclerView.adapter as FavoriteCitiesAdapter).locations = locations
        (recyclerView.adapter as FavoriteCitiesAdapter).notifyDataSetChanged()
    }

    private fun listenForMapLocationAdded() {
        mapViewModel.finalLocation.observe(viewLifecycleOwner) {
            Log.i("TAG", "listenForMapLocationAdded: ")
            it.getContentIfNotHandled()?.let { location ->
                favoritesViewModel.addFavoriteLocation(location)
            }
        }
    }

    private val showConnectionErrorDialog = {
        UIHelper.showAlertDialog(
            requireContext(),
            getString(R.string.no_connection),
            getString(R.string.no_connection_add_favorite_message)
        )
    }

}