package example.test.phong.weatherapp.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction
import example.test.phong.weatherapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val permissions: Array<String> = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (permissions.any { checkSelfPermission(it) == PERMISSION_DENIED })) {
            requestPermissions(permissions, 0)
        } else {
            supportFragmentManager.transaction(allowStateLoss = true) {
                replace(R.id.activity_main, CurrentWeatherFragment())
                addToBackStack(CurrentWeatherFragment::class.java.simpleName)
            }
        }
        setSupportActionBar(toolbar)
    }

    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            supportFragmentManager.transaction(allowStateLoss = true) {
                if (permissions.any { checkSelfPermission(it) == PERMISSION_DENIED }) {
                    replace(R.id.activity_main, NoPermissionFragment())
                } else {
                    replace(R.id.activity_main, CurrentWeatherFragment())
                    addToBackStack(CurrentWeatherFragment::class.java.simpleName)
                }
            }
        }
    }
}
