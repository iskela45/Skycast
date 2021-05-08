package fi.organization.skycast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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


        // Observer to update temperature
        model.currentTemp.observe(viewLifecycleOwner, Observer {
            var view : TextView = view.findViewById(R.id.temp)
            view.text = ("$it")
        })

        // Observer to update temperature feel
        model.currentFeel.observe(viewLifecycleOwner, Observer {
            var view : TextView = view.findViewById(R.id.feelsLike)
            view.text = "Feels like: $it"
        })

        // Observer to update description
        model.currentDesc.observe(viewLifecycleOwner, Observer {
            var view : TextView = view.findViewById(R.id.skyDesc)
            view.text = it
        })

        // Observer to update wind
        model.currentWind.observe(viewLifecycleOwner, Observer {
            var view : TextView = view.findViewById(R.id.windVal)
            view.text = it
        })

        // Observer to update humidity
        model.currentHumi.observe(viewLifecycleOwner, Observer {
            var view : TextView = view.findViewById(R.id.humiVal)
            view.text = it
        })

        // Observer to update visibility
        model.currentVis.observe(viewLifecycleOwner, Observer {
            var view : TextView = view.findViewById(R.id.visVal)
            view.text = it
        })
    }
}