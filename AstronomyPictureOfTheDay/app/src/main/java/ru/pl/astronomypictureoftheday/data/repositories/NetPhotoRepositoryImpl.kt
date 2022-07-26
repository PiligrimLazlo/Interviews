package ru.pl.astronomypictureoftheday.data.repositories

import android.util.Pair
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.data.PhotoMapper
import ru.pl.astronomypictureoftheday.data.api.PhotoApi
import ru.pl.astronomypictureoftheday.data.api.TopPhotoPagingSource
import ru.pl.astronomypictureoftheday.domain.repository.NetPhotoRepository
import ru.pl.astronomypictureoftheday.utils.JsonDateAdapter
import javax.inject.Inject

class NetPhotoRepositoryImpl @Inject constructor(
    private val photoApi: PhotoApi,
    private val mapper: PhotoMapper
) : NetPhotoRepository {

    //for paging lib
    override fun fetchPhotos(): Flow<PagingData<PhotoEntity>> {
        return Pager(
            createPagerConfig(),
            pagingSourceFactory = { TopPhotoPagingSource(photoApi) }
        ).flow
            .map { pagingData ->
                pagingData.map { topPhotoResponse ->
                    mapper.dtoToEntityPhoto(topPhotoResponse)
                }
            }
    }

    override fun fetchPhotos(startDate: Long, endDate: Long): Flow<PagingData<PhotoEntity>> {
        return Pager(
            createPagerConfig(),
            pagingSourceFactory = { TopPhotoPagingSource(photoApi, startDate to endDate) }
        ).flow
            .map { pagingData ->
                pagingData.map { topPhotoResponse ->
                    mapper.dtoToEntityPhoto(topPhotoResponse)
                }
            }
    }

    override suspend fun fetchPhoto(): PhotoEntity {
        return mapper.dtoToEntityPhoto(photoApi.fetchTopPhoto())
    }

    private fun createPagerConfig() = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE,
        prefetchDistance = PAGE_SIZE / 2,
        enablePlaceholders = false
    )


    companion object {
        const val PAGE_SIZE = 10

    }
}