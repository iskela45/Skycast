package fi.organization.skycast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    //var temp : Int = 0
    //var desc : String = "error connecting to OpenWeather"


    val currentTemp: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentFeel: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentDesc: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentWind: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentHumi: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentVis: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentIcon: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}