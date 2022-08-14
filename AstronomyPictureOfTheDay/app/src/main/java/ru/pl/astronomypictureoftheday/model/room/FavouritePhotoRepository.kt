package ru.pl.astronomypictureoftheday.model.room

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import java.util.*

class FavouritePhotoRepository private constructor(context: Context) {

    private val database: FavouritePhotoDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            FavouritePhotoDatabase::class.java,
            "Favourite photo database"
        )
        .fallbackToDestructiveMigration()
        .build()

    fun getFavouritePhotos(): Flow<List<FavouritePhoto>> =
        database.favouritePhotoDao().getFavouritePhotos()

    suspend fun getFavouritePhoto(id: Int): FavouritePhoto =
        database.favouritePhotoDao().getFavouritePhoto(id)

    suspend fun getFavouritePhoto(title: String): FavouritePhoto? =
        database.favouritePhotoDao().getFavouritePhoto(title)

    suspend fun updateFavouritePhoto(favouritePhoto: FavouritePhoto) {
        database.favouritePhotoDao().updateFavouritePhoto(favouritePhoto)
    }

    suspend fun addFavouritePhoto(favouritePhoto: FavouritePhoto) {
        database.favouritePhotoDao().addFavouritePhoto(favouritePhoto)
    }

    suspend fun deleteFavouritePhoto(favouritePhoto: FavouritePhoto) {
        database.favouritePhotoDao().deleteFavouritePhoto(favouritePhoto)
    }

    suspend fun deleteFavouritePhoto(title: String) {
        database.favouritePhotoDao().deleteFavouritePhoto(title)
    }


    companion object {
        private var INSTANCE: FavouritePhotoRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = FavouritePhotoRepository(context)
            }
        }

        fun get(): FavouritePhotoRepository {
            return INSTANCE
                ?: throw IllegalStateException("FavouritePhotoRepository must be initialized")
        }
    }
}