package ru.pl.astronomypictureoftheday.domain.usecase

import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository

class GetPhotoDbUseCase(
    private val dbPhotoRepository: DbPhotoRepository
) {

    suspend operator fun invoke(title: String): PhotoEntity? {
        return dbPhotoRepository.getPhoto(title)
    }
}