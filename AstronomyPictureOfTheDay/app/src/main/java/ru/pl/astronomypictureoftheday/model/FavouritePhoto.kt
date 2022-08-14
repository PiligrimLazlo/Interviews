package ru.pl.astronomypictureoftheday.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity
data class FavouritePhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val title: String,
    val explanation: String,
    val imageUrl: String,
    val imageHdUrl: String = "",
    var isFavourite: Boolean = false,
    val localPhotoPath: String = ""
): Parcelable