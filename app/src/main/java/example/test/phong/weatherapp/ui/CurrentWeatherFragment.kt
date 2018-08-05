package example.test.phong.weatherapp.ui


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.component1
import androidx.core.location.component2
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import com.google.android.gms.location.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import example.test.phong.weatherapp.BuildConfig
import example.test.phong.weatherapp.Converter
import example.test.phong.weatherapp.R
import example.test.phong.weatherapp.model.Current
import example.test.phong.weatherapp.net.OpenWeatherMap
import kotlinx.android.synthetic.main.fragment_current_weather.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class CurrentWeatherFragment : Fragment() {
    private lateinit var converter: Converter
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.locations?.firstOrNull()?.also { location ->
                val (latitude, longitude) = location
                retrieveForecast(latitude, longitude)
            }
        }
    }

    private var call: Call<Current>? = null
    private val cacheSize: Long = 10 * 1024 * 1024
    private val okHttpClient: OkHttpClient by lazy {
        context?.let {
            OkHttpClient.Builder()
                    .cache(Cache(it.cacheDir, cacheSize))
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
        } ?: throw IllegalStateException("context is not valid")
    }

    private val moshiConverterFactory: MoshiConverterFactory by lazy {
        MoshiConverterFactory.create(
                Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                                    )
    }

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
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this).apply {
                LocationRequest.create().apply {
                    requestLocationUpdates(this, locationCallback, null)
                }
            }
        }
    }

    override fun onPause() {
        call?.cancel()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        super.onPause()
    }


    private var currentWeather: Current? = null

    private fun retrieveForecast(latitude: Double, longitude: Double) {
        Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(moshiConverterFactory)
                .build().apply {
                    call = create(OpenWeatherMap::class.java)
                            .currentWeather(latitude, longitude, BuildConfig.API_KEY)
                }
        call?.enqueue(object : retrofit2.Callback<Current> {
            override fun onFailure(call: Call<Current>?, t: Throwable?) {
                println("Error $t")
            }

            override fun onResponse(call: Call<Current>?, response: Response<Current>?) {
                println("Got current: ${response?.body()}")
                response?.body()?.also {
                    currentWeather = it
                    bind(it)
                }
            }

        })
    }

    private fun bind(current: Current) {
        TransitionManager.beginDelayedTransition(content_panel)
        all_widgets.visibility = VISIBLE
        city.text = current.name
        temperature.text = converter.temperature(current.temperaturePressure.temperature)
        wind_speed.text = (current.wind.speed ?: 0f).let { speed ->
            converter.speed(speed)
        }
        wind_direction.rotation = current.wind.direction ?: 0f
        timestamp.text = LocalDateTime.ofInstant(current.time, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                                .withLocale(Locale.getDefault())
                                .withZone(ZoneId.systemDefault()))

        current.takeIf { it.weather.isNotEmpty() }?.apply {
            weather[0].apply {
                summary.text = main
                type_image.contentDescription = description
                type_image.setImageResource(
                        resources.getIdentifier(
                                "ic_$icon",
                                "drawable",
                                context?.packageName
                                               )
                                           )
            }
        }
    }
}
