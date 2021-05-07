package fi.organization.skycast.response


import com.google.gson.annotations.SerializedName

class Rain(
    @SerializedName("1h")
    var h: Double = 0.0
)