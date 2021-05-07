package fi.organization.skycast.response3


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h")
    val h: Double = 0.0
)