package ru.pl.astronomypictureoftheday.presentation

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import ru.pl.astronomypictureoftheday.di.DaggerApplicationComponent
import ru.pl.astronomypictureoftheday.workers.WallpaperWorkerFactory
import javax.inject.Inject

class TopPhotoApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: WallpaperWorkerFactory

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        //это для того, чтобы Dagger мог заинжектить workerFactory
        component.inject(this)
        super.onCreate()
    }


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}