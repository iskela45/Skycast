package fi.organization.skycast.response2


data class WeatherX(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)