package fi.organization.skycast.response


import com.google.gson.annotations.SerializedName

class Alert(
    @SerializedName("sender_name")
    var senderName: String = "",
    @SerializedName("event")
    var event: String = "",
    @SerializedName("start")
    var start: Int = 0,
    @SerializedName("end")
    var end: Int = 0,
    @SerializedName("description")
    var description: String = ""
)