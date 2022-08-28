package ru.pl.astronomypictureoftheday.domain.usecase

import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository
import javax.inject.Inject

class AddPhotoDbUseCase @Inject constructor(
    private val dbPhotoRepository: DbPhotoRepository
) {

    suspend operator fun invoke(photoEntity: PhotoEntity) {
        dbPhotoRepository.addPhoto(photoEntity)
    }
}