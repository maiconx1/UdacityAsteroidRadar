package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

fun Date.getFormattedDate(): String {
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(this)
}