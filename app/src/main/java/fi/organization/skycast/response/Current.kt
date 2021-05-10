package fi.organization.skycast.response


import com.google.gson.annotations.SerializedName

class Current(
    @SerializedName("dt")
    var dt: Int = 0,
    @SerializedName("sunrise")
    var sunrise: Int = 0,
    @SerializedName("sunset")
    var sunset: Int = 0,
    @SerializedName("temp")
    var temp: Double = 0.0,
    @SerializedName("feels_like")
    var feelsLike: Double = 0.0,
    @SerializedName("pressure")
    var pressure: Int = 0,
    @SerializedName("humidity")
    var humidity: Int = 0,
    @SerializedName("dew_point")
    var dewPoint: Double = 0.0,
    @SerializedName("uvi")
    var uvi: Double = 0.0,
    @SerializedName("clouds")
    var clouds: Int = 0,
    @SerializedName("visibility")
    var visibility: Double = 0.0,
    @SerializedName("wind_speed")
    var windSpeed: Double = 0.0,
    @SerializedName("wind_deg")
    var windDeg: Int = 0,
    @SerializedName("weather")
    var weather: List<Weather> = listOf(),
    @SerializedName("rain")
    var rain: Rain = Rain()
)