package fi.organization.skycast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import fi.organization.skycast.cityCoordinatesResponse.cityCords
import fi.organization.skycast.response.weatherResponse
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    val mainFrag = MainFrag()
    val weekFrag = WeekFragment()
    val settingsFrag = SettingsFrag()

    lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Define fragments

        //val weatherViewModel =  ViewModelProvider.of()
        //var weatherViewModel = ViewModelProviders.of(this).get(weatherResponse::class.java)

        // Create view model to share data to fragments
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        // Test observer for currentTemp
        weatherViewModel.currentTemp.observe(this, Observer {
            println(it.toString())
        })



        // Start with main fragment
        setCurrentFrag(mainFrag)

        // select fragment based on ID
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miMain -> setCurrentFrag(mainFrag)
                R.id.miWeek -> setCurrentFrag(weekFrag)
                R.id.miSettings -> setCurrentFrag(settingsFrag)
            }
            // lambda excepts to return true
            true
        }

        fetchJson()
        fetchCityCords()

    }

    private fun updateData(data: weatherResponse) {
        runOnUiThread() {
            weatherViewModel.currentTemp.value = data.current.temp.toInt()
            weatherViewModel.currentDesc.value = data.current.weather[0].description
        }
    }

    // Used to replace and commit changes to frame
    private fun setCurrentFrag(fragment: Fragment) {

        //Bundle
        val arguments = Bundle()
        arguments.putInt("VALUE1", 111)
        fragment.arguments = arguments
        // Replace replaces the current fragment with the given one
        // commit applies the change.
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrag, fragment)
            //addToBackStack(null)
            commit()
        }
    }

    // temp as string based on units??
    fun fetchJson() {
        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=45&lon=-94.04&units=metric&exclude=hourly,minutely&appid=7900bd079ee8808aa0a42b4e13cf1c71"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()

                // Create gson
                val gson = GsonBuilder().create()

                // create a variable from weatherResponse and the response body using gson
                val weatherObj = gson.fromJson(body, weatherResponse::class.java)
                println(weatherObj)
                println(weatherObj.current.temp)

                updateData(weatherObj)
            }

            // Triggered if the request fails.
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }

    /**
     * Used with another openWeather Api to get the coordinates of a given city.
     */
    fun fetchCityCords() {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=tampere&appid=7900bd079ee8808aa0a42b4e13cf1c71"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()

                // Create gson
                val gson = GsonBuilder().create()

                // create a variable from cityCords and the response body using gson
                val cityObj = gson.fromJson(body, cityCords::class.java)
                println("City name: " + cityObj.name)
                println("latitude: " + cityObj.coord.lat)
                println("longitude: " + cityObj.coord.lon)
            }

            // Triggered if the request fails
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute city request")
            }
        })
    }
}