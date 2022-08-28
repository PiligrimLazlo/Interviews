package ru.pl.astronomypictureoftheday.domain.usecase

import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository

class AddPhotoDbUseCase(private val dbPhotoRepository: DbPhotoRepository) {

    suspend operator fun invoke(photoEntity: PhotoEntity) {
        dbPhotoRepository.addPhoto(photoEntity)
    }
}