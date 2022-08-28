package ru.pl.astronomypictureoftheday.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.NetPhotoRepository

class FetchPhotosNetUseCase(private val netPhotoRepository: NetPhotoRepository) {

    operator fun invoke(): Flow<PagingData<PhotoEntity>> {
        return netPhotoRepository.fetchPhotos()
    }
}