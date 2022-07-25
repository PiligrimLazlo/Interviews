package ru.pl.astronomypictureoftheday.api

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.pl.astronomypictureoftheday.utils.JsonDateAdapter

class NasaPhotoRepository {
    private val nasaApi: NasaApi by lazy {
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

    suspend fun fetchTopPhoto(): TopPhotoEntity = nasaApi.fetchTopPhoto()

    suspend fun fetchTopPhoto(startDate: String, endDate: String) =
        nasaApi.fetchTopPhoto(startDate, endDate)
}