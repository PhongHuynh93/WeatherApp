package example.test.phong.weatherapp.di

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WeatherStationModule {
    @Provides
    @Singleton
    fun provideContext(application: Application) = application.applicationContext
}
