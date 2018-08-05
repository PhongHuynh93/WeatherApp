package example.test.phong.weatherapp.ui


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import example.test.phong.weatherapp.BuildConfig
import example.test.phong.weatherapp.Converter
import example.test.phong.weatherapp.R
import example.test.phong.weatherapp.location.FusedLocationProvider
import example.test.phong.weatherapp.location.LocationProvider
import example.test.phong.weatherapp.model.CurrentWeather
import example.test.phong.weatherapp.net.Current
import example.test.phong.weatherapp.net.OpenWeatherMapProvider
import kotlinx.android.synthetic.main.fragment_current_weather.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import retrofit2.Call
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class CurrentWeatherFragment : Fragment() {
    private lateinit var converter: Converter
    private lateinit var localtionProvider: LocationProvider

    private var call: Call<Current>? = null
    private val cacheSize: Long = 10 * 1024 * 1024
    private lateinit var currentWeatherProvider: OpenWeatherMapProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_weather, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_settings -> {
                    fragmentManager?.transaction(allowStateLoss = true) {
                        replace(R.id.activity_main, PreferencesFragment())
                        addToBackStack(PreferencesFragment::class.java.simpleName)
                    }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        converter = Converter(context!!)
        localtionProvider = FusedLocationProvider(context)
        currentWeatherProvider = OpenWeatherMapProvider(context, BuildConfig.API_KEY)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        context?.apply {
            if (this is AppCompatActivity) {
                supportActionBar?.apply {
                    title = getString(R.string.current_weather)
                    setHasOptionsMenu(true)
                    setDisplayHomeAsUpEnabled(false)
                }
            }
            localtionProvider.requestUpdates(::retrieveForecast)
        }
    }

    override fun onPause() {
        call?.cancel()
        localtionProvider.cancelUpdates(::retrieveForecast)
        super.onPause()
    }

    private fun retrieveForecast(latitude: @ParameterName(name = "latitude") Double, longitude: @ParameterName(name = "longitude") Double) {
        currentWeatherProvider.request(latitude, longitude, ::bind)
    }

    private var currentWeather: Current? = null

    private fun bind(current: CurrentWeather) {
        androidx.transition.TransitionManager.beginDelayedTransition(content_panel)
        all_widgets.visibility = View.VISIBLE
        city.text = current.placeName
        temperature.text = converter.temperature(current.temperature)
        wind_speed.text = converter.speed(current.windSpeed)
        wind_direction.rotation = current.windDirection
        timestamp.text = converter.timeString(current.timestamp)
        summary.text = current.weatherType
        type_image.contentDescription = current.weatherDescription
        type_image.setImageResource(
                resources.getIdentifier(
                        "ic_${current.icon}",
                        "drawable",
                        context?.packageName
                                       )
                                   )
    }
}
