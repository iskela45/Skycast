package fi.organization.skycast

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import fi.organization.skycast.cityCoordinatesResponse.cityCords
import fi.organization.skycast.response.weatherResponse
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    // TODO: Set "sensible" default values for all fragment views.
    // TODO: refresh weather data
    // TODO: Imperial AM/PM?
    val mainFrag = MainFrag()
    val weekFrag = WeekFragment()
    val preferencesFrag = PreferencesFrag()
    var measurementSystem = "metric"
    var dist = " km"
    var degr = "°C"
    var speed = " m/s"
    val COARSE_LOCATION_RQ = 101

    lateinit var weatherViewModel: WeatherViewModel
    lateinit var weekViewModel: WeekViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var bottomNavigationView: BottomNavigationView

    //private var listener: OnSharedPreferenceChangeListener? = null
    //lateinit var var prefs = PreferenceManager.getDefaultSharedPreferences(this)
    //var prefs = PreferenceManager.getDefaultSharedPreferences(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        println("CREATED")
        super.onCreate(savedInstanceState)

        registerPreferenceListener()

        setContentView(R.layout.activity_main)

        supportActionBar?.subtitle = "Your location"
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Create view model to share data to fragments
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weekViewModel = ViewModelProvider(this).get(WeekViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Read unit preferences from settings, metric by default.
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var unitPref = prefs?.getString("MEASUREMENTS", "metric")
        if (unitPref == "imperial") checkImperial() else checkMetric()

        // Start with main fragment
        setCurrentFrag(mainFrag)

        // Create preference editor
        var prefsEditor = prefs.edit()
        // Set mainFrag as the default fragment when the app is started.
        prefsEditor.putString("FRAGMENT", "mainFrag")

        // Select fragment based on ID and save the current fragment into preferences.
        // Preference value gets read in onResume()
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miMain -> {
                    setCurrentFrag(mainFrag)
                    prefsEditor.putString("FRAGMENT", "mainFrag")
                }
                R.id.miWeek -> {
                    setCurrentFrag(weekFrag)
                    prefsEditor.putString("FRAGMENT", "weekFrag")
                }
                R.id.miSettings -> {
                    setCurrentFrag(preferencesFrag)
                    prefsEditor.putString("FRAGMENT", "preferencesFrag")
                }
                //R.id.miSettings -> setCurrentFrag(settingsFrag)
            }
            prefsEditor.commit()
            // lambda excepts to return true
            true
        }
        //fetchCityCords()

    }

    override fun onDestroy() {
        unregisterPreferenceListener()
        super.onDestroy()
    }

    // Load most recent fragment from preferences
    override fun onResume() {
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var prefFrag = prefs?.getString("FRAGMENT", "mainFrag")

        when (prefFrag) {
            "mainFrag" -> bottomNavigationView.selectedItemId = R.id.miMain
            "weekFrag" -> bottomNavigationView.selectedItemId = R.id.miWeek
            "preferencesFrag" -> bottomNavigationView.selectedItemId = R.id.miSettings
        }

        super.onResume()
    }

    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                    // Toast
                    Toast.makeText(applicationContext, "Checking location data", Toast.LENGTH_SHORT).show()
                    // ask location
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            // Put latitude and longitude into an array
                            // 0.0 if location data is null
                            var locArray = arrayOf<Double>(
                                location?.latitude ?: 0.0,
                                location?.longitude ?: 0.0
                            )

                            fetchJson(measurementSystem, locArray)

                        }

                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
                )

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("This app requires your location data to function properly")
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
            }
        }

        when (requestCode) {
            COARSE_LOCATION_RQ -> innerCheck("location")
        }
    }

    private fun updateData(data: weatherResponse) {
        runOnUiThread() {

            // Convert visibility distance from metres to km or miles
            data.current.visibility = distanceConverter(data.current.visibility, dist)

            // Put data type suffixes into the weatherViewModel
            weatherViewModel.suffixDist.value = dist
            weatherViewModel.suffixSpeed.value = speed
            weatherViewModel.suffixTemp.value = degr

            // Put temperature suffix into the weekViewModel
            weekViewModel.suffixTemp.value = degr

            // Put weather and timezone data into the weatherViewModel
            weatherViewModel.timezone.value = data.timezoneOffset
            weatherViewModel.currentWeather.value = data.current

            // Put timezone and a list of 8 days into viewModel
            weekViewModel.timezone.value = data.timezoneOffset
            weekViewModel.dailyWeather.value = data.daily
        }
    }

    // Used to replace and commit changes to frame
    private fun setCurrentFrag(fragment: Fragment) {
        // Replace replaces the current fragment with the given one
        // commit applies the change.
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrag, fragment)
            commit()
        }
    }

    fun checkMetric() {
        dist = " km"
        degr = "°C"
        speed = " m/s"
        measurementSystem = "metric"
        checkForPermissions(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            "location",
            COARSE_LOCATION_RQ
        )

    }

    fun checkImperial() {
        dist = " miles"
        degr = "°F"
        speed = " mph"
        measurementSystem = "imperial"
        checkForPermissions(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            "location",
            COARSE_LOCATION_RQ
        )
    }

    fun fetchJson(unitType: String, cords: Array<Double>) {
        var lat = cords[0]
        var lon = cords[1]
        // TODO: Hide the APIKey and generate a new one since this has gone to github.
        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&units=$unitType&exclude=hourly,minutely&appid=7900bd079ee8808aa0a42b4e13cf1c71"
        println("url: $url")

        println("lat: $lat")
        println("lon: $lon")

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()

                // Create gson
                val gson = GsonBuilder().create()

                // create a variable from weatherResponse and the response body using gson
                val weatherObj = gson.fromJson(body, weatherResponse::class.java)

                // Send the data to the viewModels
                updateData(weatherObj)
            }

            // Triggered if the request fails.
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }

    private fun registerPreferenceListener() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun unregisterPreferenceListener() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "MEASUREMENTS") {
            var x = sharedPreferences?.getString("MEASUREMENTS", "metric")
            if (x == "imperial") checkImperial() else checkMetric()
        }
    }



}