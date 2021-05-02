package fi.organization.skycast.response


import com.google.gson.annotations.SerializedName

data class WeatherX(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)