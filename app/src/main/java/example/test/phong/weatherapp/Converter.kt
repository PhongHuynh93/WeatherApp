package example.test.phong.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Converter(
        val context: Context,
        private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
               ) {

    fun speed(value: Float) : String =
            sharedPreferences.getString("Speed", "mph").let {units ->
                when (units) {
                    "mph" -> msToMph(value)
                    else -> value
                }
            }.let {
                context.getString(R.string.wind_speed, it)
            }

    fun temperature(value: Float): String =
            sharedPreferences.getString("Temperature", "celcius").let {
                when (it) {
                    "celcius" -> kelvinToCelsius(value) to R.string.temperature_celsius
                        else -> kelvinToFahrenheit(value) to R.string.temperature_fahrenheit
                }
            }.let {(newValue, template) ->
                context.getString(template, newValue)
            }


    private fun kelvinToCelsius(value: Float): Float {
        return value - 273.15f
    }

    private fun kelvinToFahrenheit(value: Float): Float = (9f / 5f * (value - 273.15f)) + 32


    private fun msToMph(value: Float): Float {
        return value * 2.2369362920544f
    }
}