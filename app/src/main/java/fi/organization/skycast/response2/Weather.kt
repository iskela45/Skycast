package fi.organization.skycast.response2


data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)