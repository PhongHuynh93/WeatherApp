package example.test.phong.weatherapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.test.phong.weatherapp.model.CurrentWeather
import javax.inject.Inject

class CurrentWeatherViewModel @Inject constructor(val currentWeather: LiveData<CurrentWeather>): ViewModel() {
}