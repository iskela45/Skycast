package fi.organization.skycast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    //var temp : Int = 0
    //var desc : String = "error connecting to OpenWeather"


    val currentTemp: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentDesc: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}