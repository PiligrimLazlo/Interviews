package ru.pl.astronomypictureoftheday.domain.usecase

import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository
import javax.inject.Inject

class GetPhotoDbUseCase @Inject constructor(
    private val dbPhotoRepository: DbPhotoRepository
) {

    suspend operator fun invoke(title: String): PhotoEntity? {
        return dbPhotoRepository.getPhoto(title)
    }
}