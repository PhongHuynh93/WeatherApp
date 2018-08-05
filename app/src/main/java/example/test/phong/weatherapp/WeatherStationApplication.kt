package example.test.phong.weatherapp

import android.app.Application
import androidx.fragment.app.Fragment
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import example.test.phong.weatherapp.di.DaggerWeatherStationComponent
import example.test.phong.weatherapp.di.WeatherStationComponent
import javax.inject.Inject

class WeatherStationApplication: Application(), HasSupportFragmentInjector {
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private val weatherStationComponent: WeatherStationComponent by lazy {
        DaggerWeatherStationComponent.builder()
                .application(this)
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        weatherStationComponent.inject(this)
        AndroidThreeTen.init(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }
}