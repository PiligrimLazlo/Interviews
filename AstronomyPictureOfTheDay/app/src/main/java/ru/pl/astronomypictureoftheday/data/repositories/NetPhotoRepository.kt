package ru.pl.astronomypictureoftheday.data.repositories

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
import ru.pl.astronomypictureoftheday.data.PhotoEntity
import ru.pl.astronomypictureoftheday.data.PhotoMapper
import ru.pl.astronomypictureoftheday.data.api.TopPhotoApi
import ru.pl.astronomypictureoftheday.data.api.TopPhotoPagingSource
import ru.pl.astronomypictureoftheday.utils.JsonDateAdapter

class NetPhotoRepository private constructor() {
    //todo передавать в конструкторе
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
    private val mapper: PhotoMapper = PhotoMapper()


    //for paging lib
    fun fetchPhotos(): Flow<PagingData<PhotoEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TopPhotoPagingSource(topPhotoApi) }
        ).flow
            .map { pagingData ->
                pagingData.map { topPhotoResponse ->
                    mapper.dtoToEntityPhoto(topPhotoResponse)
                }
            }
    }

    suspend fun fetchPhoto(): PhotoEntity = mapper.dtoToEntityPhoto(topPhotoApi.fetchTopPhoto())


    companion object {

        private var INSTANCE: NetPhotoRepository? = null

        const val PAGE_SIZE = 10

        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = NetPhotoRepository()
            }
        }

        fun get(): NetPhotoRepository {
            return INSTANCE
                ?: throw IllegalStateException("NetPhotoRepository must be initialized")
        }
    }
}