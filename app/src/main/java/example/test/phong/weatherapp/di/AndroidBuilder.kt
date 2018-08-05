package example.test.phong.weatherapp.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import example.test.phong.weatherapp.ui.CurrentWeatherFragment

@Module
abstract class AndroidBuilder {
    @ContributesAndroidInjector
    abstract fun bindCurrentWeatherFragment(): CurrentWeatherFragment


}