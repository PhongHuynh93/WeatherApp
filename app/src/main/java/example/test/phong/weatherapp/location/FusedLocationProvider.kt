package example.test.phong.weatherapp.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.location.component1
import androidx.core.location.component2
import com.google.android.gms.location.*

class FusedLocationProvider(private val context: Context,
                            private var fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)) : LocationProvider {

    private val permissions: Array<out String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
                                                        )

    private val subscribers = mutableListOf<(latitude: Double, longitude: Double) -> Unit>()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.firstOrNull()?.also { location ->
                val (latitude, longitude) = location
                subscribers.forEach { callback ->
                    callback(latitude, longitude)
                }
            }
        }
    }

    override fun requestUpdates(callback: (latitude: Double, longitude: Double) -> Unit) {
        if (subscribers.isEmpty()) {
            subscribers += callback
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissions.all { context.checkSelfPermission(it) == PERMISSION_GRANTED }) {
                    LocationRequest.create().apply {
                        fusedLocationProvider.requestLocationUpdates(this, locationCallback, null)
                    }
                }
            } else {
                LocationRequest.create().apply {
                    fusedLocationProvider.requestLocationUpdates(this, locationCallback, null)
                }
            }
        } else {
            subscribers += callback
        }
    }

    override fun cancelUpdates(callback: (latitude: Double, longitude: Double) -> Unit) {
        subscribers -= callback
        if (subscribers.isEmpty()) {
            fusedLocationProvider.removeLocationUpdates(locationCallback)
        }
    }
}
