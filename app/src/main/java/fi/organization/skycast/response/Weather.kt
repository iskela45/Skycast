package fi.organization.skycast.response


import com.google.gson.annotations.SerializedName

class Weather(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("main")
    var main: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("icon")
    var icon: String = ""
)