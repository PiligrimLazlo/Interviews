package ru.pl.astronomypictureoftheday.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "saved_photo_table")
data class PhotoDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val title: String,
    val explanation: String,
    val imageUrl: String,
    val imageHdUrl: String = "",
    var isFavourite: Boolean = false,
    val cachePhotoPath: String = ""
) {

}