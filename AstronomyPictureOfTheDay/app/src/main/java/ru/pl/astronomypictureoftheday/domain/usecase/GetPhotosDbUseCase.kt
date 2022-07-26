package ru.pl.astronomypictureoftheday.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository
import javax.inject.Inject

class GetPhotosDbUseCase @Inject constructor(
    private val dbPhotoRepository: DbPhotoRepository
) {

    operator fun invoke(): Flow<List<PhotoEntity>> {
        return dbPhotoRepository.getPhotos()
    }

}