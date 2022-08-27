package ru.pl.astronomypictureoftheday

import android.app.Application
import ru.pl.astronomypictureoftheday.data.repositories.PreferencesRepository
import ru.pl.astronomypictureoftheday.data.repositories.DbPhotoRepository
import ru.pl.astronomypictureoftheday.data.repositories.NetPhotoRepository

class TopPhotoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
        DbPhotoRepository.initialize(this)
        NetPhotoRepository.initialize()
    }
}