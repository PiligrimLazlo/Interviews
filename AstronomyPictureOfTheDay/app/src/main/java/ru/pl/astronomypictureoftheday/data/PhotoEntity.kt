package ru.pl.astronomypictureoftheday.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoEntity(
    val formattedDate: String,
    val title: String,
    val explanation: String,
    val imageUrl: String,
    val imageHdUrl: String = "",
    var isFavourite: Boolean = false,
    val cachePhotoPath: String = ""
): Parcelable