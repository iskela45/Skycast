package fi.organization.skycast.response3


import com.google.gson.annotations.SerializedName

data class Daily(
    val dt: Int = 0,
    val sunrise: Int = 0,
    val sunset: Int = 0,
    val moonrise: Int = 0,
    val moonset: Int = 0,
    @SerializedName("moon_phase")
    val moonPhase: Double = 0.0,
    val temp: Temp = Temp(),
    @SerializedName("feels_like")
    val feelsLike: FeelsLike = FeelsLike(),
    val pressure: Int = 0,
    val humidity: Int = 0,
    @SerializedName("dew_point")
    val dewPoint: Double = 0.0,
    @SerializedName("wind_speed")
    val windSpeed: Double = 0.0,
    @SerializedName("wind_deg")
    val windDeg: Int = 0,
    @SerializedName("wind_gust")
    val windGust: Double = 0.0,
    val weather: List<WeatherX> = listOf(),
    val clouds: Int = 0,
    val pop: Double = 0.0,
    val rain: Double = 0.0,
    val snow: Double = 0.0,
    val uvi: Double = 0.0
)