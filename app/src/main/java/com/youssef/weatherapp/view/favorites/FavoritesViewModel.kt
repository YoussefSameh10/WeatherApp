package com.youssef.weatherapp.view.favorites

import android.util.Log
import androidx.lifecycle.*
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.utils.Event
import com.youssef.weatherapp.utils.Formatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesViewModel(private val repo: RepositoryInterface, private val owner: LifecycleOwner) : ViewModel() {

    val TAG = "Favorites"

    private var _favoriteLocations: MutableLiveData<List<Location>> = MutableLiveData()
    val favoriteLocations: LiveData<List<Location>> get() = _favoriteLocations

    private var _cityName: MutableLiveData<Event<String>> = MutableLiveData()

    private val formatter = Formatter(repo)
    
    fun getFavoriteLocations() {

        Log.i(TAG, "getFavoriteLocations: ")
        var locations: LiveData<List<Location>> = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            locations = repo.getFavoriteLocations()
            withContext(Dispatchers.Main) {
                locations.removeObservers(owner)
                locations.observe(owner) {
                    _favoriteLocations.postValue(it)
                    Log.i(TAG, "getFavoriteLocations: $it")
                }
            }
        }

    }

    fun addFavoriteLocation(location: Location) {
        _cityName.removeObservers(owner)
        _cityName.observe(owner) {
            it.getContentIfNotHandled()?.let { cityName ->
                Log.i(TAG, "addFavoriteLocation: $cityName")
                viewModelScope.launch(Dispatchers.IO) {
                    location.name = formatter.formatCityName(cityName)
                    location.isCurrent = false
                    Log.i(TAG, "addFavoriteLocation: $location")
                    repo.addFavoriteLocation(location)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "getCityName: ${repo.getCityName(location.latitude, location.longitude)}")
            _cityName.postValue(Event(repo.getCityName(location.latitude, location.longitude)))
        }
    }

    fun deleteFavoriteLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavoriteLocation(location)
        }
    }
}