package example.test.phong.weatherapp.model

import androidx.lifecycle.LiveData
import example.test.phong.weatherapp.location.LocationProvider
import example.test.phong.weatherapp.net.CurrentWeatherProvider
import javax.inject.Inject

class CurrentWeatherLiveData @Inject constructor(private val locationProvider: LocationProvider,
                                                 private val currentWeatherProvider: CurrentWeatherProvider): LiveData<CurrentWeather>() {
    override fun onActive() {
        super.onActive()
        locationProvider.requestUpdates(::updateLocation)
    }

    override fun onInactive() {
        currentWeatherProvider.cancel()
        locationProvider.cancelUpdates(::updateLocation)
        super.onInactive()
    }

    private fun updateLocation(latitude: @ParameterName(name = "latitude") Double, longitude: @ParameterName(name = "longitude") Double) {
        currentWeatherProvider.request(latitude, longitude) {
            postValue(it)
        }
    }
}