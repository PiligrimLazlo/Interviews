package ru.pl.astronomypictureoftheday.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class TopPhotoEntity(
    val date: Date,
    val title: String,
    val explanation: String,
    @Json(name = "url") val imageUrl: String
)