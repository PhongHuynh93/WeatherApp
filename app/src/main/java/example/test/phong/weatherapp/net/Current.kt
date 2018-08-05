package example.test.phong.weatherapp.net

import com.squareup.moshi.Json
import example.test.phong.weatherapp.model.CurrentWeather
import org.threeten.bp.Instant

// used in under class
data class Current(
        @Json(name = "coord") val coordinates: Coordinates,
        @Json(name = "weather") val weather: List<Weather>,
        @Json(name = "main") val temperaturePressure: TemperaturePressure,
        @Json(name = "wind") val wind: Wind,
        @Json(name = "dt") val timestamp: Long,
        @Json(name = "name") val name: String
                  ) {
    val time: Instant by lazy {
        Instant.ofEpochSecond(timestamp)
    }

    // used in upper class
    val currentWeather = CurrentWeather(
            coordinates.latitude,
            coordinates.longitude,
            name,
            temperaturePressure.temperature,
            wind.speed ?: 0f,
            wind.direction ?: 0f,
            weather[0].main,
            weather[0].description,
            weather[0].icon,
            time
                                       )
}