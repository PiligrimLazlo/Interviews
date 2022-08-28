package ru.pl.astronomypictureoftheday.domain.usecase

import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository

class DeletePhotoDbUseCase(private val dbPhotoRepository: DbPhotoRepository) {

    suspend operator fun invoke(title: String) {
        dbPhotoRepository.deletePhoto(title)
    }
}