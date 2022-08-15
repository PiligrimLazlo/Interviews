package ru.pl.astronomypictureoftheday

import android.app.Application
import ru.pl.astronomypictureoftheday.model.repositories.PreferencesRepository
import ru.pl.astronomypictureoftheday.model.repositories.FavouritePhotoRepository

class TopPhotoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
        FavouritePhotoRepository.initialize(this)
    }
}