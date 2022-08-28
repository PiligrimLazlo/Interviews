package ru.pl.astronomypictureoftheday.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.PhotoEntity

interface NetPhotoRepository {

    suspend fun fetchPhoto(): PhotoEntity

    fun fetchPhotos(): Flow<PagingData<PhotoEntity>>

    //todo доределать paging source, чтобы он мог работать не только с бесконечной датой, как сейчас
    //todo а так же с диапазоном дат
    //fun fetchPhotos(startDate: String, endDate: String): Flow<List<PhotoEntity>>
}