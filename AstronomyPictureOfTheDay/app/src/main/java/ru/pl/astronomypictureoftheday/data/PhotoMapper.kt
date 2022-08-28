package ru.pl.astronomypictureoftheday.data

import ru.pl.astronomypictureoftheday.data.api.PhotoDto
import ru.pl.astronomypictureoftheday.data.room.PhotoDbModel
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import java.text.SimpleDateFormat
import java.util.*

class PhotoMapper {

    fun dtoToEntityPhoto(photoDto: PhotoDto) = PhotoEntity(
        formattedDate = dateToFormattedDateString(photoDto.date),
        title = photoDto.title,
        explanation = photoDto.explanation,
        imageUrl = photoDto.imageUrl,
        imageHdUrl = photoDto.imageHdUrl
    )

    fun entityToDbModelPhoto(photoEntity: PhotoEntity) = PhotoDbModel(
        date = formattedDateStringToDate(photoEntity.formattedDate, DATE_FORMAT),
        title = photoEntity.title,
        explanation = photoEntity.explanation,
        imageUrl = photoEntity.imageUrl,
        imageHdUrl = photoEntity.imageHdUrl,
        isFavourite = photoEntity.isFavourite,
        cachePhotoPath = photoEntity.cachePhotoPath
    )

    fun dbModelToEntityPhoto(photoDbModel: PhotoDbModel) = PhotoEntity(
        formattedDate = dateToFormattedDateString(photoDbModel.date),
        title = photoDbModel.title,
        explanation = photoDbModel.explanation,
        imageUrl = photoDbModel.imageUrl,
        imageHdUrl = photoDbModel.imageHdUrl,
        isFavourite = photoDbModel.isFavourite,
        cachePhotoPath = photoDbModel.cachePhotoPath
    )

    fun dbModelToEntityPhotoList(dbPhotos: List<PhotoDbModel>): List<PhotoEntity> {
        return dbPhotos.map {
            dbModelToEntityPhoto(it)
        }
    }

    private fun dateToFormattedDateString(date: Date): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(date)
    }

    private fun formattedDateStringToDate(dateString: String, format: String): Date {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.parse(dateString)
            ?: throw IllegalArgumentException("Could not parse date, wrong format!")
    }

    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy"
    }
}