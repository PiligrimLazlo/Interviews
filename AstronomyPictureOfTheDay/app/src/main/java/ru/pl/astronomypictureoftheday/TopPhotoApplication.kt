package ru.pl.astronomypictureoftheday

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ru.pl.astronomypictureoftheday.model.PreferencesRepository

class TopPhotoApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        PreferencesRepository.initialize(this)
    }
}