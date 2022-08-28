package ru.pl.astronomypictureoftheday.presentation

import android.app.Application
import ru.pl.astronomypictureoftheday.data.repositories.PreferencesRepositoryImpl
import ru.pl.astronomypictureoftheday.data.repositories.DbPhotoRepositoryIml
import ru.pl.astronomypictureoftheday.data.repositories.NetPhotoRepositoryImpl

class TopPhotoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepositoryImpl.initialize(this)
        DbPhotoRepositoryIml.initialize(this)
        NetPhotoRepositoryImpl.initialize()
    }
}