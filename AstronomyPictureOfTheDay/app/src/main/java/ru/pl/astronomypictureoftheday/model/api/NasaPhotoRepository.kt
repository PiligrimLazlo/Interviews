package ru.pl.astronomypictureoftheday.model.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.pl.astronomypictureoftheday.model.TopPhotoEntity
import ru.pl.astronomypictureoftheday.model.TopPhotoPagingSource
import ru.pl.astronomypictureoftheday.utils.JsonDateAdapter

class NasaPhotoRepository {
    private val topPhotoApi: TopPhotoApi by lazy {
        val moshiBuilder = Moshi.Builder().add(JsonDateAdapter())

        val logging = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
        val httpClient = OkHttpClient.Builder().apply { addInterceptor(logging) }
        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://api.nasa.gov/planetary/")
            .addConverterFactory(MoshiConverterFactory.create(moshiBuilder.build()))
            .client(httpClient.build())
            .build()
        retrofit.create()
    }

    suspend fun fetchTopPhoto(): TopPhotoEntity = topPhotoApi.fetchTopPhotos()

    suspend fun fetchTopPhoto(startDate: String, endDate: String) =
        topPhotoApi.fetchTopPhotos(startDate, endDate)

    //for paging lib
    fun fetchTopPhotos(): Flow<PagingData<TopPhotoEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TopPhotoPagingSource(topPhotoApi) }
        ).flow
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}