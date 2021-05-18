package fi.organization.skycast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class WeekFragment : Fragment(R.layout.fragment_week) {

    // Create ViewModel
    private val model : WeekViewModel by activityViewModels()

    /**
     * Update title and subtitle
     * Create viewModel liveData observer that updates the UI with new data
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set title and subtitle
        (activity as AppCompatActivity)?.supportActionBar?.title = "Skycast"
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = "Upcoming weather"

        // Observer checking currentWeather liveData for changes.
        // If changes occur iterate through all day.xml views with up-to-date data
        model.dailyWeather.observe(viewLifecycleOwner, Observer {
            for ((i, day) in it.withIndex()) {
                // Select a card corresponding to a day based on the iteration.
                val resId = resources.getIdentifier("day$i", "id", requireActivity().getPackageName())
                val card : View = view.findViewById(resId)

                // Assign views inside the card
                val temp : TextView = card.findViewById(R.id.textView_temperature)
                val desc : TextView = card.findViewById(R.id.textView_condition)
                val date : TextView = card.findViewById(R.id.textView_date)
                var icon : ImageView = card.findViewById(R.id.imageView_condition_icon)

                // Assign values to the views
                temp.text = (day.temp.max.toInt().toString() + model.suffixTemp.value)
                desc.text = day.weather[0].description
                date.text = dateFormat(day.dt, model.timezone.value ?: 0)
                icon.setImageResource(getImage(day.weather[0].icon))
            }
        })
    }
}