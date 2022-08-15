package ru.pl.astronomypictureoftheday.model.api

import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "g2i26rNZnPGzrK7pV62jkvSwj3aKPwojszearVQ3"

interface TopPhotoApi {

    @GET("apod?api_key=$API_KEY")
    suspend fun fetchTopPhotos(): TopPhotoResponse

    @GET("apod?api_key=$API_KEY")
    suspend fun fetchTopPhotos(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): List<TopPhotoResponse>


}