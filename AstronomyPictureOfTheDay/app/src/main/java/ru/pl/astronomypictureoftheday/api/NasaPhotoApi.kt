package ru.pl.astronomypictureoftheday.api

import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "g2i26rNZnPGzrK7pV62jkvSwj3aKPwojszearVQ3"

interface NasaApi {

    @GET("apod?api_key=$API_KEY")
    suspend fun fetchTopPhoto(): TopPhotoEntity

    @GET("apod?api_key=$API_KEY")
    suspend fun fetchTopPhoto(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): List<TopPhotoEntity>


}