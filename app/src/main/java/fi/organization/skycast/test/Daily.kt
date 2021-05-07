package fi.organization.skycast.test


import com.google.gson.annotations.SerializedName

class Daily(
    @SerializedName("dt")
    var dt: Int = 0,
    @SerializedName("sunrise")
    var sunrise: Int = 0,
    @SerializedName("sunset")
    var sunset: Int = 0,
    @SerializedName("moonrise")
    var moonrise: Int = 0,
    @SerializedName("moonset")
    var moonset: Int = 0,
    @SerializedName("moon_phase")
    var moonPhase: Double = 0.0,
    @SerializedName("temp")
    var temp: Temp = Temp(),
    @SerializedName("feels_like")
    var feelsLike: FeelsLike = FeelsLike(),
    @SerializedName("pressure")
    var pressure: Int = 0,
    @SerializedName("humidity")
    var humidity: Int = 0,
    @SerializedName("dew_point")
    var dewPoint: Double = 0.0,
    @SerializedName("wind_speed")
    var windSpeed: Double = 0.0,
    @SerializedName("wind_deg")
    var windDeg: Int = 0,
    @SerializedName("wind_gust")
    var windGust: Double = 0.0,
    @SerializedName("weather")
    var weather: List<WeatherX> = listOf(),
    @SerializedName("clouds")
    var clouds: Int = 0,
    @SerializedName("pop")
    var pop: Double = 0.0,
    @SerializedName("snow")
    var snow: Double = 0.0,
    @SerializedName("uvi")
    var uvi: Double = 0.0,
    @SerializedName("rain")
    var rain: Double = 0.0
)