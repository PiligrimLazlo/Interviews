package ru.pl.astronomypictureoftheday.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.NetPhotoRepository
import javax.inject.Inject

class FetchRangePhotosNetUseCase @Inject constructor(
    private val netPhotoRepository: NetPhotoRepository
) {

    operator fun invoke(startDate: Long, endDate: Long): Flow<PagingData<PhotoEntity>> {
        return netPhotoRepository.fetchPhotos(startDate, endDate)
    }
}