package ru.pl.astronomypictureoftheday

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ru.pl.astronomypictureoftheday.model.PreferencesRepository
import ru.pl.astronomypictureoftheday.model.room.FavouritePhotoRepository

class TopPhotoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
        FavouritePhotoRepository.initialize(this)
    }
}