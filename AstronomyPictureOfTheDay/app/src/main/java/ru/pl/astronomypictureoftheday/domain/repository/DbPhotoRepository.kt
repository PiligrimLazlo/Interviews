package ru.pl.astronomypictureoftheday.domain.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.data.room.PhotoDbModel
import ru.pl.astronomypictureoftheday.domain.PhotoEntity

interface DbPhotoRepository {

    fun getPhotos(): Flow<List<PhotoEntity>>

    suspend fun getPhoto(title: String): PhotoEntity?

    suspend fun addPhoto(photoEntity: PhotoEntity)

    suspend fun deletePhoto(title: String)
}