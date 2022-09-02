package ru.pl.astronomypictureoftheday.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.PhotoEntity

interface NetPhotoRepository {

    suspend fun fetchPhoto(): PhotoEntity

    fun fetchPhotos(): Flow<PagingData<PhotoEntity>>

    fun fetchPhotos(startDate: Long, endDate: Long): Flow<PagingData<PhotoEntity>>
}