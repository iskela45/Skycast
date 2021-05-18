package fi.organization.skycast

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class MainFrag : Fragment(R.layout.fragment_main) {

    // Create ViewModel
    private val model : WeatherViewModel by activityViewModels()

    /**
     * Update title and subtitle
     * Create viewModel liveData observer that updates the UI with new data
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set title and subtitle
        (activity as AppCompatActivity)?.supportActionBar?.title = "Skycast"
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = "Current weather"

        // Create views
        val viewTemp : TextView = view.findViewById(R.id.temp)
        val viewFeel : TextView = view.findViewById(R.id.feelsLike)
        val viewDesc : TextView = view.findViewById(R.id.skyDesc)
        val viewWind : TextView = view.findViewById(R.id.windVal)
        val viewHumi : TextView = view.findViewById(R.id.humiVal)
        val viewVisi : TextView = view.findViewById(R.id.visVal)
        val viewTime : TextView = view.findViewById(R.id.updatedData)

        // Observer checking currentWeather liveData for changes.
        // If changes occur updates textViews with up-to-date data
        model.currentWeather.observe(viewLifecycleOwner, Observer {
            viewTemp.text = (it.temp.toInt().toString() + model.suffixTemp.value)
            viewFeel.text = ("Feels like: " + (it.feelsLike.toInt().toString() + model.suffixTemp.value))
            viewDesc.text = it.weather[0].description
            viewWind.text = (String.format("%.1f", it.windSpeed) + model.suffixSpeed.value)
            viewHumi.text = (it.humidity.toString() + "%")
            viewVisi.text = (String.format("%.1f", it.visibility) + model.suffixDist.value)
            viewTime.text = timeFormat(it.dt, model.timezone.value ?: 0, true)
        })
    }
}