package fi.organization.skycast

// Convert Unix time to a DateTime format, then split at T to only send the date back.
fun timeFormat(dt: Int, offset: Int): String {
    var t = java.time.format.DateTimeFormatter.ISO_INSTANT
        .format(java.time.Instant.ofEpochSecond(dt.toLong() + offset.toLong()))

    return t.split("T")[0]
}

// Return a drawable based on the image code given.
fun getImage(image: String) : Int =
    when (image) {
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
