package ru.pl.astronomypictureoftheday.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDbDao {

    @Query("SELECT * FROM saved_photo_table")
    fun getPhotos(): Flow<List<PhotoDbModel>>

    @Query("SELECT * FROM saved_photo_table WHERE title=:title")
    suspend fun getPhoto(title: String): PhotoDbModel?

    @Update
    suspend fun updatePhoto(photoDbModel: PhotoDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(photoDbModel: PhotoDbModel)

    @Delete
    suspend fun deletePhoto(photoDbModel: PhotoDbModel)

    @Query ("DELETE FROM saved_photo_table WHERE title=:title")
    suspend fun deletePhoto(title: String)

    @Query("DELETE FROM saved_photo_table")
    suspend fun clearAllPhotos()
}