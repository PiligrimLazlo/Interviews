package ru.pl.astronomypictureoftheday.model

import ru.pl.astronomypictureoftheday.model.api.TopPhotoResponse
import ru.pl.astronomypictureoftheday.model.room.SavedPhotoDbEntity

class PhotoMapper {

    fun responseToFavouritePhoto(photoResponse: TopPhotoResponse) = FavouritePhoto(
        date = photoResponse.date,
        title = photoResponse.title,
        explanation = photoResponse.explanation,
        imageUrl = photoResponse.imageUrl,
        imageHdUrl = photoResponse.imageHdUrl
    )

    fun favouriteToSavedPhoto(favouritePhoto: FavouritePhoto) = SavedPhotoDbEntity(
        date = favouritePhoto.date,
        title = favouritePhoto.title,
        explanation = favouritePhoto.explanation,
        imageUrl = favouritePhoto.imageUrl,
        imageHdUrl = favouritePhoto.imageHdUrl,
        isFavourite = favouritePhoto.isFavourite,
        localPhotoPath = favouritePhoto.localPhotoPath
    )

    fun savedToFavouritePhoto(savedPhotoDbEntity: SavedPhotoDbEntity) = FavouritePhoto(
        date = savedPhotoDbEntity.date,
        title = savedPhotoDbEntity.title,
        explanation = savedPhotoDbEntity.explanation,
        imageUrl = savedPhotoDbEntity.imageUrl,
        imageHdUrl = savedPhotoDbEntity.imageHdUrl,
        isFavourite = savedPhotoDbEntity.isFavourite,
        localPhotoPath = savedPhotoDbEntity.localPhotoPath
    )

    fun savedPhotoListToFavouritePhotoList(dbPhotos: List<SavedPhotoDbEntity>): List<FavouritePhoto> {
        return dbPhotos.map {
            savedToFavouritePhoto(it)
        }
    }
}