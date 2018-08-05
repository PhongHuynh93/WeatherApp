package example.test.phong.weatherapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class WeatherStationApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}