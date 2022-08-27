package ru.pl.astronomypictureoftheday.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class PhotoDto(
    val date: Date,
    val title: String,
    val explanation: String,
    @Json(name = "url") val imageUrl: String,
    @Json(name = "hdurl") val imageHdUrl: String = ""
)