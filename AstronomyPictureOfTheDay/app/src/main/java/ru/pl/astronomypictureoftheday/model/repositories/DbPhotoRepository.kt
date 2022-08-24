package ru.pl.astronomypictureoftheday.model.repositories

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.pl.astronomypictureoftheday.model.PhotoEntity
import ru.pl.astronomypictureoftheday.model.PhotoMapper
import ru.pl.astronomypictureoftheday.model.room.PhotoDatabase

class DbPhotoRepository private constructor(context: Context) {

    //todo передавать в конструкторе
    private val database: PhotoDatabase by lazy {
        Room.databaseBuilder(
                context.applicationContext,
                PhotoDatabase::class.java,
                "saved_photo_db"
            )
            .fallbackToDestructiveMigration()
            .build()
    }
    private val mapper: PhotoMapper = PhotoMapper()

    fun getPhotos(): Flow<List<PhotoEntity>>  =
        database.photoDao().getPhotos().map {
            mapper.dbModelToEntityPhotoList(it)
        }

    suspend fun getPhoto(id: Int): PhotoEntity? {
        val savedPhoto = database.photoDao().getPhoto(id) ?: return null
        return mapper.dbModelToEntityPhoto(savedPhoto)
    }

    suspend fun getPhoto(title: String): PhotoEntity? {
        val savedPhoto = database.photoDao().getPhoto(title) ?: return null
        return mapper.dbModelToEntityPhoto(savedPhoto)
    }

    suspend fun updatePhoto(photoEntity: PhotoEntity) {
        database.photoDao().updatePhoto(mapper.entityToDbModelPhoto(photoEntity))
    }

    suspend fun addPhoto(photoEntity: PhotoEntity) {
        database.photoDao().addPhoto(mapper.entityToDbModelPhoto(photoEntity))
    }

    suspend fun deletePhoto(photoEntity: PhotoEntity) {
        database.photoDao().deletePhoto(mapper.entityToDbModelPhoto(photoEntity))
    }

    suspend fun deletePhoto(title: String) {
        database.photoDao().deletePhoto(title)
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