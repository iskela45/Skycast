package fi.organization.skycast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.organization.skycast.response.Daily

class WeekViewModel : ViewModel() {
    //var temp : Int = 0
    //var desc : String = "error connecting to OpenWeather"


    val dailyWeather: MutableLiveData<List<Daily>> by lazy {
        MutableLiveData<List<Daily>>()
    }
}