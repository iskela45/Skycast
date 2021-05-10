package fi.organization.skycast

import java.text.SimpleDateFormat
import java.util.*

/** This file includes functions that'll be needed in multiple classes. */

/**
 * Convert Unix time to a DateTime format, then split at T to only send the date back.
 *
 * @param dt Unix timestamp
 * @param offset Unix timestamp timezone offset
 */
fun dateFormat(dt: Int, offset: Int): String {
    var t = java.time.format.DateTimeFormatter.ISO_INSTANT
        .format(java.time.Instant.ofEpochSecond(dt.toLong() + offset.toLong()))

    return t.split("T")[0]
}

/**
 * Convert a given distance in metres to distance in km or miles.
 *
 * @param distance distance to be converted (in metres)
 * @param newFormat String that assigns the desired conversion.
 */
fun distanceConverter(distance: Double, newFormat: String) : Double =
    when (newFormat) {
        " km" -> distance / 1000.0
        " miles" -> distance * 0.00062137
        else -> distance
    }

/**
 * Convert Unix time to a DateTime format,
 * Then split at T and only return substring containing hours and minutes.
 *
 * @param dt Unix timestamp
 * @param offset Unix timestamp timezone offset
 */
fun timeFormat(dt: Int, offset: Int, isoTime: Boolean): String {
    var t = java.time.format.DateTimeFormatter.ISO_INSTANT
        .format(java.time.Instant.ofEpochSecond(dt.toLong() + offset.toLong()))



    var time = t.split("T")[1].substring(0, 5)
    //val clock24 = SimpleDateFormat("HH:mm")
    //val clock12 = SimpleDateFormat("hh:mm a")
    //val ret: Date = clock24.parse(time)

    println("dt: $dt")
    println("tz: $offset")
    println("t: $time")
    return time
}

/**
 * Return a drawable based on the icon code given.
 * If no matches are found return the clear sky icon as a placeholder.
 *
 * @param iconCode code given by the openWeather API
 */
fun getImage(iconCode: String) : Int =
    when (iconCode) {
        "01d" -> R.drawable.ow01d
        "01n" -> R.drawable.ow01n
        "02d" -> R.drawable.ow02d
        "02n" -> R.drawable.ow02n
        "03d" -> R.drawable.ow03d
        "03n" -> R.drawable.ow03n
        "04d" -> R.drawable.ow04d
        "04n" -> R.drawable.ow04n
        "09d" -> R.drawable.ow09d
        "09n" -> R.drawable.ow09n
        "10d" -> R.drawable.ow10d
        "10n" -> R.drawable.ow10n
        "11d" -> R.drawable.ow11d
        "11n" -> R.drawable.ow11n
        "13d" -> R.drawable.ow13d
        "13n" -> R.drawable.ow13n
        "50d" -> R.drawable.ow50d
        "50n" -> R.drawable.ow50n
        else  -> R.drawable.ow01d
}
