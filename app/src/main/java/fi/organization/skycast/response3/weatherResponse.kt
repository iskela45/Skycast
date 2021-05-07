package fi.organization.skycast.response3


import com.google.gson.annotations.SerializedName

data class weatherResponse(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val timezone: String = "",
    @SerializedName("timezone_offset")
    val timezoneOffset: Int = 0,
    val current: Current = Current(),
    val daily: List<Daily> = listOf(),
    val alerts: List<Alert> = listOf()
)