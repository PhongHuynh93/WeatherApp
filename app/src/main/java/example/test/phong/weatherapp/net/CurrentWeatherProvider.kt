package example.test.phong.weatherapp.net

import example.test.phong.weatherapp.model.CurrentWeather

interface CurrentWeatherProvider {
    fun request(latitude: Double, longitude: Double, callback: (CurrentWeather) -> Unit)
    fun cancel()
}
