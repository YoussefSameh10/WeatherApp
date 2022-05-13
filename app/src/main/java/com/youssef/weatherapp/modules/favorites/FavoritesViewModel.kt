package com.youssef.weatherapp.modules.favorites

import android.util.Log
import androidx.lifecycle.*
import com.youssef.weatherapp.model.pojo.Location
import com.youssef.weatherapp.model.repo.RepositoryInterface
import com.youssef.weatherapp.model.repo.locationrepo.LocationRepositoryInterface
import com.youssef.weatherapp.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesViewModel(private val repo: LocationRepositoryInterface, private val owner: LifecycleOwner) : ViewModel() {


    private var _favoriteLocations: MutableLiveData<List<Location>> = MutableLiveData()
    val favoriteLocations: LiveData<List<Location>> get() = _favoriteLocations

    private var _cityName: MutableLiveData<Event<String>> = MutableLiveData()

    var isFavoriteExist: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getFavoriteLocations() {

        Log.i("TAG", "getFavoriteLocations: ")
        var locations: LiveData<List<Location>> = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            locations = repo.getFavoriteLocations()
            withContext(Dispatchers.Main) {
                locations.removeObservers(owner)
                locations.observe(owner) {
                    if(it != null && it.size != 0) {
                        _favoriteLocations.postValue(it)
                        isFavoriteExist.postValue(true)
                        Log.i("TAG", "getFavoriteLocations: $it")
                    }
                    else {
                        _favoriteLocations.postValue(it)
                        isFavoriteExist.postValue(false)
                    }
                }
            }
        }
    }

    fun addFavoriteLocation(location: Location) {
        _cityName.removeObservers(owner)
        _cityName.observe(owner) {
            it.getContentIfNotHandled()?.let { cityName ->
                Log.i("TAG", "addFavoriteLocation: $cityName")
                viewModelScope.launch(Dispatchers.IO) {
                    location.name = cityName
                    location.isCurrent = false
                    Log.i("TAG", "addFavoriteLocation: $location")
                    repo.addFavoriteLocation(location)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "getCityName: ${repo.getCityName(location.latitude, location.longitude)}")
            _cityName.postValue(Event(repo.getCityName(location.latitude, location.longitude)))
        }
    }

    fun deleteFavoriteLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavoriteLocation(location)
        }
    }
}