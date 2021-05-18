package fi.organization.skycast

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import fi.organization.skycast.response.weatherResponse
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    // Fragments
    private val mainFrag = MainFrag()
    private val weekFrag = WeekFragment()
    private val preferencesFrag = PreferencesFrag()
    // Measurement units that'll be sent to fragments
    var dist = " km"
    var degr = "°C"
    var speed = " m/s"

    private val FINE_LOCATION_RQ = 101

    // ViewModels
    lateinit var weatherViewModel: WeatherViewModel
    lateinit var weekViewModel: WeekViewModel

    // Google play services fusedLocationClient for location data
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // SwipeRefreshLayout for refreshing the app with new data.
    private lateinit var swipeRefresh: SwipeRefreshLayout

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var prefs : SharedPreferences
    lateinit var locationRequest: LocationRequest
    var dataLoaded = false

    /**
     * Used when requesting to start location updates.
     * Updates the current data when first loaded, then start updating new data to preferences.
     */
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            // Call fetchJson using the latest location if no data has been loaded and data is not null.
            val loc = locationResult.locations.lastOrNull()
            if(loc != null && !dataLoaded) {
                fetchJson(loc.latitude, loc.longitude)
                dataLoaded = true
            }

            // When a new location is added update it into preferences.
            // New location data will be used when refreshing or changing measurement settings.
            for (location in locationResult.locations) {
                val prefsEditor = prefs.edit()
                prefsEditor.putString("LAT", location.latitude.toString())
                prefsEditor.putString("LON", location.longitude.toString())
                prefsEditor.apply()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerPreferenceListener()
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Create view model to share data to fragments
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weekViewModel = ViewModelProvider(this).get(WeekViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize and configure locationRequest, use high accuracy to get cords quickly
        locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 300
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Read unit preferences from settings, metric by default, update activity values.
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val unitPref = prefs.getString("MEASUREMENTS", "metric")
        if (unitPref == "imperial") {
            dist = " miles"
            degr = "°F"
            speed = " mph"
        } else {
            dist = " km"
            degr = "°C"
            speed = " m/s"
        }

        // Start with main fragment
        setCurrentFrag(mainFrag)

        // Create preference editor and set mainFrag as the default fragment when the app starts.
        val prefsEditor = prefs.edit()
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
            }
            prefsEditor.apply()
            // lambda excepts to return true
            true
        }

        // Set swipe to refresh view to call for a new json.
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            val refreshUnitPref = prefs.getString("MEASUREMENTS", "metric")
            if (refreshUnitPref == "imperial") checkImperial() else checkMetric()
        }
    }

    /**
     * Unregister preference listener.
     */
    override fun onDestroy() {
        unregisterPreferenceListener()
        super.onDestroy()
    }

    /**
     * Stop listening to location updates when the app isn't being used.
     */
    override fun onPause() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onPause()
    }

    /**
     * Load most recent fragment from preferences and set it as the active fragment.
     * Restart locationUpdates by checking for permissions and then calling requestLocationUpdates()
     */
    override fun onResume() {
        when (prefs.getString("FRAGMENT", "mainFrag")) {
            "mainFrag" -> bottomNavigationView.selectedItemId = R.id.miMain
            "weekFrag" -> bottomNavigationView.selectedItemId = R.id.miWeek
            "preferencesFrag" -> bottomNavigationView.selectedItemId = R.id.miSettings
        }

        checkForPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                "location",
                FINE_LOCATION_RQ
        )

        super.onResume()
    }

    /**
     * Replace the current fragment with a new one.
     *
     * @param fragment the new fragment the UI will switch to.
     */
    private fun setCurrentFrag(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrag, fragment)
            commit()
        }
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
            // Start requesting locationUpdates.
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }

            // Open dialog box asking the user for permission
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
     *
     * @param permission permission type, in this case it's always coarse location.
     * @param name the name of the permission used in dialog boxes and toasts.
     * @param requestCode a request code.
     */
    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("This app requires your $name data to function properly")
            setTitle("Permission required")
            setPositiveButton("OK") { _, _ ->
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
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    }
                }
            }
        }

        when (requestCode) {
            FINE_LOCATION_RQ -> innerCheck("location")
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
        val prefLat : Double = prefs.getString("LAT", "0")?.toDouble() ?: 0.0
        val prefLon : Double = prefs.getString("LON", "0")?.toDouble() ?: 0.0
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
        val prefLat : Double = prefs.getString("LAT", "0")?.toDouble() ?: 0.0
        val prefLon : Double = prefs.getString("LON", "0")?.toDouble() ?: 0.0
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
        val unitType = prefs.getString("MEASUREMENTS", "metric")

        // API_KEY is found in local.properties.
        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&units=$unitType&exclude=hourly,minutely&appid=${BuildConfig.API_KEY}"

        //Create and use OkHttpClient.
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            // Called when a response is successfully received.
            override fun onResponse(call: Call, response: Response) {
                // Stop swipe refresh animation and toast.
                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false
                    runOnUiThread() { Toast.makeText(applicationContext, "Refresh successful", Toast.LENGTH_SHORT).show() }
                }

                // Response body to string,
                // then create a variable from weatherResponse and the response body using Gson.
                val body = response.body?.string()
                val gson = GsonBuilder().create()
                val weatherObj = gson.fromJson(body, weatherResponse::class.java)

                // Send the data to the viewModels
                updateData(weatherObj)
            }

            // Triggered if the request fails.
            // Toast to tell the user there is a connection problem and stop refresh animation.
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread() { Toast.makeText(applicationContext, "Connection to openWeather failed", Toast.LENGTH_SHORT).show() }
                if (swipeRefresh.isRefreshing) swipeRefresh.isRefreshing = false
            }
        })
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

            // Put data type suffixes, weather and timezone into the weatherViewModel
            weatherViewModel.suffixDist.value = dist
            weatherViewModel.suffixSpeed.value = speed
            weatherViewModel.suffixTemp.value = degr
            weatherViewModel.timezone.value = data.timezoneOffset
            weatherViewModel.currentWeather.value = data.current

            // Put temperature suffixes, weather for 8 days and timezone into the weekViewModel
            weekViewModel.suffixTemp.value = degr
            weekViewModel.timezone.value = data.timezoneOffset
            weekViewModel.dailyWeather.value = data.daily
        }
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
            val x = sharedPreferences?.getString("MEASUREMENTS", "metric")
            if (x == "imperial") checkImperial() else checkMetric()
        }
    }
}