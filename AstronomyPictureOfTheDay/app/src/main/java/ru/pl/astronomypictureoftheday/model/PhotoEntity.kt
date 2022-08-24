package ru.pl.astronomypictureoftheday.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

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