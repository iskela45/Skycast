package fi.organization.skycast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import fi.organization.skycast.databinding.ActivityMainBinding
import fi.organization.skycast.databinding.FragmentMainBinding
import fi.organization.skycast.response2.weatherResponse

class MainFrag : Fragment(R.layout.fragment_main) {
    private val model : WeatherViewModel by activityViewModels()

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