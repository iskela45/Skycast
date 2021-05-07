package fi.organization.skycast.response3


import com.google.gson.annotations.SerializedName

data class Alert(
    @SerializedName("sender_name")
    val senderName: String = "",
    val event: String = "",
    val start: Int = 0,
    val end: Int = 0,
    val description: String = ""
)