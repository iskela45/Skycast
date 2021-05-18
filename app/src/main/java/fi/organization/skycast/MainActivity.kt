package fi.organization.skycast

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import fi.organization.skycast.response.weatherResponse
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    private val mainFrag = MainFrag()
    private val weekFrag = WeekFragment()
    private val preferencesFrag = PreferencesFrag()
    var dist = " km"
    var degr = "°C"
    var speed = " m/s"
    private val COARSE_LOCATION_RQ = 101

    lateinit var weatherViewModel: WeatherViewModel
    lateinit var weekViewModel: WeekViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var swipeRefresh: SwipeRefreshLayout

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var prefs : SharedPreferences
    lateinit var locationRequest: LocationRequest

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult == null) return

            // Call fetchJson using the latest location result, if the list is empty use 0.0
            val loc = locationResult.locations.lastOrNull()
            fetchJson(loc?.latitude ?: 0.0, loc?.longitude ?: 0.0)

            // When a new location is added update it into preferences.
            // New location data will be used when refreshing or changing measurement settings.
            for (location in locationResult.locations) {
                println("location: $location")
                val prefsEditor = prefs.edit()
                prefsEditor.putString("LAT", location.latitude.toString())
                prefsEditor.putString("LON", location.longitude.toString())
                prefsEditor.apply()
            }
        }
    }


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

        // Initialize and configure locationRequest
        locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 300
        locationRequest.priority = LocationRequest.PRIORITY_LOW_POWER // city level accuracy

        // Read unit preferences from settings, metric by default.
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val unitPref = prefs?.getString("MEASUREMENTS", "metric")
        if (unitPref == "imperial") checkImperial() else checkMetric()

        // Start with main fragment
        setCurrentFrag(mainFrag)

        // Create preference editor
        val prefsEditor = prefs.edit()
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
            prefsEditor.apply()
            // lambda excepts to return true
            true
        }

        // Set swipe to refresh view to call for a new json.
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            val unitPref = prefs?.getString("MEASUREMENTS", "metric")
            if (unitPref == "imperial") checkImperial() else checkMetric()
        }
    }

    override fun onDestroy() {
        unregisterPreferenceListener()
        super.onDestroy()
    }

    override fun onPause() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onPause()
    }

    /**
     * Load most recent fragment from preferences and set it as the active fragment.
     * Restart locationUpdates by checking for permissions and then calling requestLocationUpdates()
     */
    override fun onResume() {

        when (prefs?.getString("FRAGMENT", "mainFrag")) {
            "mainFrag" -> bottomNavigationView.selectedItemId = R.id.miMain
            "weekFrag" -> bottomNavigationView.selectedItemId = R.id.miWeek
            "preferencesFrag" -> bottomNavigationView.selectedItemId = R.id.miSettings
        }

        checkForPermissions(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                "location",
                COARSE_LOCATION_RQ
        )

        super.onResume()
    }

    /**
     * If permissions are granted start locationUpdates.
     * If permissions aren't granted ask for permissions.
     *
     * @param permission permission type, in this case it's always coarse location.
     * @param name the name of the permission used in dialog boxes and toasts.
     * @param requestCode a request code.
     */
    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        when {
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                // ask location
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
            )

            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    /**
     * Create a dialog asking if the user will grant location permission
     */
    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("This app requires your $name data to function properly")
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


    /**
     * Function is called when the result from permission request is done
     *
     * if the response is negative a toast saying so is created.
     * if the response is positive a toast saying so is created and data is updated based on user preferences.
     */
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
                // Load measurement system setting and update data if the permission is successful
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val unitPref = prefs?.getString("MEASUREMENTS", "metric")
                if (unitPref == "imperial") checkImperial() else checkMetric()
            }
        }

        when (requestCode) {
            COARSE_LOCATION_RQ -> innerCheck("location")
        }
    }

    /**
     * Receives data that is then updated for the weatherViewModel and weekViewModel.
     * The viewModels use liveData to keep the UI up to date with listeners in fragments.
     *
     * @param data is an object containing all of the weather data
     */
    private fun updateData(data: weatherResponse) {
        runOnUiThread() {
            // Convert visibility distance from metres to km or miles
            data.current.visibility = distanceConverter(data.current.visibility, dist)

            // Put data type suffixes into the weatherViewModel
            weatherViewModel.suffixDist.value = dist
            weatherViewModel.suffixSpeed.value = speed
            weatherViewModel.suffixTemp.value = degr
            // Put weather and timezone data into the weatherViewModel
            weatherViewModel.timezone.value = data.timezoneOffset
            weatherViewModel.currentWeather.value = data.current

            // Put temperature suffix into the weekViewModel
            weekViewModel.suffixTemp.value = degr
            // Put timezone and a list of 8 days into viewModel
            weekViewModel.timezone.value = data.timezoneOffset
            weekViewModel.dailyWeather.value = data.daily
        }
    }

    /**
     * Replace the current fragment with a new one.
     *
     * @param fragment the new fragment the UI will switch to.
     */
    private fun setCurrentFrag(fragment: Fragment) {
        // Replace replaces the current fragment with the given one
        // commit applies the change.
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrag, fragment)
            commit()
        }
    }

    /**
     * Update measurement units and call fetchJson using location data from preferences
     */
    private fun checkMetric() {
        // Update measurement units
        dist = " km"
        degr = "°C"
        speed = " m/s"

        // Read data from preferences and call fetchJson() using the data.
        val prefLat : Double = prefs?.getString("LAT", "0")?.toDouble() ?: 0.0
        val prefLon : Double = prefs?.getString("LON", "0")?.toDouble() ?: 0.0
        fetchJson(prefLat, prefLon)

    }

    /**
     * Update measurement units and call fetchJson using location data from preferences
     */
    private fun checkImperial() {
        // Update measurement units
        dist = " miles"
        degr = "°F"
        speed = " mph"

        // Read data from preferences and call fetchJson() using the data.
        val prefLat : Double = prefs?.getString("LAT", "0")?.toDouble() ?: 0.0
        val prefLon : Double = prefs?.getString("LON", "0")?.toDouble() ?: 0.0
        fetchJson(prefLat, prefLon)

    }

    /**
     * Call openWeather oneCall api using coordinates.
     * convert the received data and put it into an object using GSON.
     *
     * @param lat latitude coordinate
     * @param lon longitude coordinate
     */
    fun fetchJson(lat: Double, lon: Double) {
        // Load measurement preferences.
        val unitType = prefs?.getString("MEASUREMENTS", "metric")

        // TODO: Hide the APIKey and generate a new one since this has gone to github.
        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&units=$unitType&exclude=hourly,minutely&appid=7900bd079ee8808aa0a42b4e13cf1c71"
        println("url: $url")

        println("lat: $lat")
        println("lon: $lon")

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            // Called when a response is successfully received.
            override fun onResponse(call: Call, response: Response) {
                // Stop swipe refresh animation
                if (swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false

                // Response body to string
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
                // Toast to tell the user there is a connection problem
                Toast.makeText(applicationContext, "Connection to openWeather failed", Toast.LENGTH_SHORT).show()

                // Stop swipe refresh animation
                if (swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false
            }
        })
    }

    /**
     * Register a preferenceListener, called in onCreate()
     */
    private fun registerPreferenceListener() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * Register a preferenceListener, called when the app is closed.
     */
    private fun unregisterPreferenceListener() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * Called when the user changes measurement system in settings.
     * call checkImperial or checkMetric according to the new preference value.
     *
     * @param sharedPreferences contains preference data.
     * @param key Key of the changed preference.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "MEASUREMENTS") {
            var x = sharedPreferences?.getString("MEASUREMENTS", "metric")
            if (x == "imperial") checkImperial() else checkMetric()
        }
    }



}