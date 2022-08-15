package ru.pl.astronomypictureoftheday.model.repositories

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
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.model.TopPhotoPagingSource
import ru.pl.astronomypictureoftheday.model.api.TopPhotoApi
import ru.pl.astronomypictureoftheday.utils.JsonDateAdapter

class NasaPhotoRepository {
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


    //for paging lib
    fun fetchTopPhotos(): Flow<PagingData<FavouritePhoto>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TopPhotoPagingSource(topPhotoApi) }
        ).flow
            .map { it.map { it.toFavouritePhoto() } }
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}