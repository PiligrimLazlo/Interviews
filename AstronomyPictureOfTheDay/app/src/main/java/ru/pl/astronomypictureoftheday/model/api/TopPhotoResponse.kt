package ru.pl.astronomypictureoftheday.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import java.util.*

@JsonClass(generateAdapter = true)
data class TopPhotoResponse(
    val date: Date,
    val title: String,
    val explanation: String,
    @Json(name = "url") val imageUrl: String,
    @Json(name = "hdurl") val imageHdUrl: String = ""
) {

    fun toFavouritePhoto(): FavouritePhoto = FavouritePhoto(
        date = date,
        title = title,
        explanation = explanation,
        imageUrl = imageUrl,
        imageHdUrl = imageHdUrl
    )

}