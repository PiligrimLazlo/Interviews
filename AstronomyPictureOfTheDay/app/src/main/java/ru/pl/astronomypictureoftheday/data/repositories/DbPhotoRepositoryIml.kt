package ru.pl.astronomypictureoftheday.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.pl.astronomypictureoftheday.data.PhotoMapper
import ru.pl.astronomypictureoftheday.data.room.PhotoDbDao
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository
import javax.inject.Inject

class DbPhotoRepositoryIml @Inject constructor(
    private val dao: PhotoDbDao,
    private val mapper: PhotoMapper
) : DbPhotoRepository {


    override fun getPhotos(): Flow<List<PhotoEntity>> =
        dao.getPhotos().map {
            mapper.dbModelToEntityPhotoList(it)
        }


    override suspend fun getPhoto(title: String): PhotoEntity? {
        val savedPhoto = dao.getPhoto(title) ?: return null
        return mapper.dbModelToEntityPhoto(savedPhoto)
    }


    override suspend fun addPhoto(photoEntity: PhotoEntity) {
        dao.addPhoto(mapper.entityToDbModelPhoto(photoEntity))
    }


    override suspend fun deletePhoto(title: String) {
        dao.deletePhoto(title)
    }


    //todo убрать
//    companion object {
//        private var INSTANCE: DbPhotoRepositoryIml? = null
//
//        fun initialize(context: Context) {
//            if (INSTANCE == null) {
//                INSTANCE = DbPhotoRepositoryIml(context)
//            }
//        }
//
//        fun get(): DbPhotoRepositoryIml {
//            return INSTANCE
//                ?: throw IllegalStateException("FavouritePhotoRepository must be initialized")
//        }
//    }
}