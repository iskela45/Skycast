package fi.organization.skycast.response3


data class FeelsLike(
    val day: Double = 0.0,
    val night: Double = 0.0,
    val eve: Double = 0.0,
    val morn: Double = 0.0
)