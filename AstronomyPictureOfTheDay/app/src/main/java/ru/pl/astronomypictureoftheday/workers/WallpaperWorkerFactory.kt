package ru.pl.astronomypictureoftheday.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.pl.astronomypictureoftheday.domain.usecase.FetchPhotoNetUseCase
import ru.pl.astronomypictureoftheday.utils.ImageManager
import javax.inject.Inject

class WallpaperWorkerFactory @Inject constructor(
    private val imageManager: ImageManager,
    private val fetchPhotoNetUseCase: FetchPhotoNetUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return WallpaperWorker(appContext, workerParameters, imageManager, fetchPhotoNetUseCase)
    }
}