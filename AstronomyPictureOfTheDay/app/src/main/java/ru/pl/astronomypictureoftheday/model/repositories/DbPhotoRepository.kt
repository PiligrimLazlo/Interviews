package ru.pl.astronomypictureoftheday.model.repositories

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.model.PhotoMapper
import ru.pl.astronomypictureoftheday.model.room.SavedPhotoDatabase

class DbPhotoRepository private constructor(context: Context) {

    //todo передавать в конструкторе
    private val database: SavedPhotoDatabase by lazy {
        Room.databaseBuilder(
                context.applicationContext,
                SavedPhotoDatabase::class.java,
                "saved_photo_db"
            )
            .fallbackToDestructiveMigration()
            .build()
    }
    private val mapper: PhotoMapper = PhotoMapper()

    fun getFavouritePhotos(): Flow<List<FavouritePhoto>>  =
        database.savedPhotoDao().getPhotos().map {
            mapper.savedPhotoListToFavouritePhotoList(it)
        }

    suspend fun getFavouritePhoto(id: Int): FavouritePhoto? {
        val savedPhoto = database.savedPhotoDao().getPhoto(id) ?: return null
        return mapper.savedToFavouritePhoto(savedPhoto)
    }

    suspend fun getFavouritePhoto(title: String): FavouritePhoto? {
        val savedPhoto = database.savedPhotoDao().getPhoto(title) ?: return null
        return mapper.savedToFavouritePhoto(savedPhoto)
    }

    suspend fun updateFavouritePhoto(favouritePhoto: FavouritePhoto) {
        database.savedPhotoDao().updatePhoto(mapper.favouriteToSavedPhoto(favouritePhoto))
    }

    suspend fun addFavouritePhoto(favouritePhoto: FavouritePhoto) {
        database.savedPhotoDao().addPhoto(mapper.favouriteToSavedPhoto(favouritePhoto))
    }

    suspend fun deleteFavouritePhoto(favouritePhoto: FavouritePhoto) {
        database.savedPhotoDao().deletePhoto(mapper.favouriteToSavedPhoto(favouritePhoto))
    }

    suspend fun deleteFavouritePhoto(title: String) {
        database.savedPhotoDao().deletePhoto(title)
    }


    companion object {
        private var INSTANCE: DbPhotoRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DbPhotoRepository(context)
            }
        }

        fun get(): DbPhotoRepository {
            return INSTANCE
                ?: throw IllegalStateException("FavouritePhotoRepository must be initialized")
        }
    }
}