package fi.organization.skycast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fi.organization.skycast.response.Daily

class WeekViewModel : ViewModel() {

    val dailyWeather: MutableLiveData<List<Daily>> by lazy {
        MutableLiveData<List<Daily>>()
    }

    val timezone: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val suffixTemp: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}