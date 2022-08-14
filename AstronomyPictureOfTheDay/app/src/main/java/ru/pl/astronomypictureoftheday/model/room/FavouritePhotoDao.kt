package ru.pl.astronomypictureoftheday.model.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE
import ru.pl.astronomypictureoftheday.model.FavouritePhoto

@Dao
interface FavouritePhotoDao {

    @Query("SELECT * FROM favouritephoto")
    fun getFavouritePhotos(): Flow<List<FavouritePhoto>>

    @Query("SELECT * FROM favouritephoto WHERE id=:id")
    suspend fun getFavouritePhoto(id: Int): FavouritePhoto

    @Query("SELECT * FROM favouritephoto WHERE title=:title")
    suspend fun getFavouritePhoto(title: String): FavouritePhoto?

    @Update
    suspend fun updateFavouritePhoto(favouritePhoto: FavouritePhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavouritePhoto(favouritePhoto: FavouritePhoto)

    @Delete
    suspend fun deleteFavouritePhoto(favouritePhoto: FavouritePhoto)

    @Query ("DELETE FROM favouritePhoto WHERE title=:title")
    suspend fun deleteFavouritePhoto(title: String)

    @Query("DELETE FROM favouritePhoto")
    suspend fun clearAllFavouritePhotos()
}