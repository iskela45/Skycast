package fi.organization.skycast.test


import com.google.gson.annotations.SerializedName

class test(
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("lon")
    var lon: Double = 0.0,
    @SerializedName("timezone")
    var timezone: String = "",
    @SerializedName("timezone_offset")
    var timezoneOffset: Int = 0,
    @SerializedName("current")
    var current: Current = Current(),
    @SerializedName("daily")
    var daily: List<Daily> = listOf(),
    @SerializedName("alerts")
    var alerts: List<Alert> = listOf()
)