package fi.organization.skycast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val currentMeasurements: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}