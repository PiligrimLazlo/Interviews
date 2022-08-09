package ru.pl.astronomypictureoftheday.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@JsonClass(generateAdapter = true)
@Parcelize
data class TopPhotoEntity(
    val date: Date,
    val title: String,
    val explanation: String,
    @Json(name = "url") val imageUrl: String,
    @Json(name = "hdurl") val imageHdUrl: String = ""
) : Parcelable