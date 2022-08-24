package ru.pl.astronomypictureoftheday.model

import ru.pl.astronomypictureoftheday.model.api.PhotoDto
import ru.pl.astronomypictureoftheday.model.room.PhotoDbModel

class PhotoMapper {

    fun dtoToEntityPhoto(photoDto: PhotoDto) = PhotoEntity(
        date = photoDto.date,
        title = photoDto.title,
        explanation = photoDto.explanation,
        imageUrl = photoDto.imageUrl,
        imageHdUrl = photoDto.imageHdUrl
    )

    fun entityToDbModelPhoto(photoEntity: PhotoEntity) = PhotoDbModel(
        date = photoEntity.date,
        title = photoEntity.title,
        explanation = photoEntity.explanation,
        imageUrl = photoEntity.imageUrl,
        imageHdUrl = photoEntity.imageHdUrl,
        isFavourite = photoEntity.isFavourite,
        localPhotoPath = photoEntity.localPhotoPath
    )

    fun dbModelToEntityPhoto(photoDbModel: PhotoDbModel) = PhotoEntity(
        date = photoDbModel.date,
        title = photoDbModel.title,
        explanation = photoDbModel.explanation,
        imageUrl = photoDbModel.imageUrl,
        imageHdUrl = photoDbModel.imageHdUrl,
        isFavourite = photoDbModel.isFavourite,
        localPhotoPath = photoDbModel.localPhotoPath
    )

    fun dbModelToEntityPhotoList(dbPhotos: List<PhotoDbModel>): List<PhotoEntity> {
        return dbPhotos.map {
            dbModelToEntityPhoto(it)
        }
    }
}