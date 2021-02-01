package com.udacity.asteroidradar.domain

import com.squareup.moshi.Json

data class PictureOfDay(
    @field:Json(name = "media_type") val mediaType: String, val title: String,
    val url: String
)