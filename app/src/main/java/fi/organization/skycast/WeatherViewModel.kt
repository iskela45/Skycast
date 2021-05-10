package fi.organization.skycast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.organization.skycast.response.Current
import fi.organization.skycast.response.Daily

class WeatherViewModel : ViewModel() {
    //var temp : Int = 0
    //var desc : String = "error connecting to OpenWeather"


    val currentWeather: MutableLiveData<Current> by lazy {
        MutableLiveData<Current>()
    }

    val suffixTemp: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val suffixSpeed: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val suffixDist: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val timezone: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
}