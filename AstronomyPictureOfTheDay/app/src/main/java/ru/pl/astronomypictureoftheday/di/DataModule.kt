package ru.pl.astronomypictureoftheday.di

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.pl.astronomypictureoftheday.data.api.PhotoApi
import ru.pl.astronomypictureoftheday.data.api.PhotoApiFactory
import ru.pl.astronomypictureoftheday.data.repositories.DbPhotoRepositoryIml
import ru.pl.astronomypictureoftheday.data.repositories.NetPhotoRepositoryImpl
import ru.pl.astronomypictureoftheday.data.repositories.PreferencesRepositoryImpl
import ru.pl.astronomypictureoftheday.data.room.PhotoDatabase
import ru.pl.astronomypictureoftheday.data.room.PhotoDbDao
import ru.pl.astronomypictureoftheday.domain.repository.DbPhotoRepository
import ru.pl.astronomypictureoftheday.domain.repository.NetPhotoRepository
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesRepository

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindNetPhotoRepository(netPhotoRepositoryImpl: NetPhotoRepositoryImpl): NetPhotoRepository

    @Binds
    @ApplicationScope
    fun bindDbPhotoRepository(DbPhotoRepositoryIml: DbPhotoRepositoryIml): DbPhotoRepository


    companion object {

        @Provides
        @ApplicationScope
        fun providePhotoApi(): PhotoApi {
            return PhotoApiFactory.photoApi
        }

        @Provides
        @ApplicationScope
        fun providePhotoDao(application: Application): PhotoDbDao {
            return PhotoDatabase.getInstance(application).photoDao()
        }

        @Provides
        @ApplicationScope
        fun providePreferencesRepository(application: Application): PreferencesRepository {
            PreferencesRepositoryImpl.initialize(application)
            return PreferencesRepositoryImpl.get()
        }


    }
}