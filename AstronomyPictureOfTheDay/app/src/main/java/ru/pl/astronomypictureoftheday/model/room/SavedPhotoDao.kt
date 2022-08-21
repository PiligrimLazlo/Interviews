package ru.pl.astronomypictureoftheday.model.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPhotoDao {

    @Query("SELECT * FROM saved_photo_table")
    fun getPhotos(): Flow<List<SavedPhotoDbEntity>>

    @Query("SELECT * FROM saved_photo_table WHERE id=:id")
    suspend fun getPhoto(id: Int): SavedPhotoDbEntity?

    @Query("SELECT * FROM saved_photo_table WHERE title=:title")
    suspend fun getPhoto(title: String): SavedPhotoDbEntity?

    @Update
    suspend fun updatePhoto(savedPhotoDbEntity: SavedPhotoDbEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(savedPhotoDbEntity: SavedPhotoDbEntity)

    @Delete
    suspend fun deletePhoto(savedPhotoDbEntity: SavedPhotoDbEntity)

    @Query ("DELETE FROM saved_photo_table WHERE title=:title")
    suspend fun deletePhoto(title: String)

    @Query("DELETE FROM saved_photo_table")
    suspend fun clearAllPhotos()
}