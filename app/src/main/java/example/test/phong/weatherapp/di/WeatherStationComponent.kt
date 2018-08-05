package example.test.phong.weatherapp.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import example.test.phong.weatherapp.WeatherStationApplication
import example.test.phong.weatherapp.location.LocationModule
import example.test.phong.weatherapp.net.WeatherModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AndroidBuilder::class,
    WeatherStationModule::class,
    LocationModule::class,
    WeatherModule::class
])
interface WeatherStationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): WeatherStationComponent
    }

    fun inject(application: WeatherStationApplication)
}