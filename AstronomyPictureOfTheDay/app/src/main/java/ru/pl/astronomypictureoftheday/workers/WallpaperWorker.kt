package ru.pl.astronomypictureoftheday.workers

import android.app.WallpaperManager
import android.content.Context
import androidx.work.*
import ru.pl.astronomypictureoftheday.domain.usecase.FetchPhotoNetUseCase
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.util.concurrent.TimeUnit

class WallpaperWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters,
    private val imageManager: ImageManager,
    private val fetchPhotoNetUseCase: FetchPhotoNetUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val photo = fetchPhotoNetUseCase()

        val wallpaperManager = WallpaperManager.getInstance(context)
        val filePath = imageManager.getInternalImageFullPathFileHd(photo.title, context.filesDir)
        val bitmap = imageManager.loadPhotoFromCache(filePath)
            ?: imageManager.loadBitmapFromNet(photo.imageHdUrl)
            ?: return Result.failure()
        return try {
            val scaledImageData = imageManager.scaleBitmapForWallpapers(bitmap)
            wallpaperManager.setBitmap(
                scaledImageData.bitmap,
                scaledImageData.rect,
                false,
                WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
            )

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val AUTO_SET_WALLPAPER = "AUTO_SET_WALLPAPER"
        private const val INITIAL_DELAY = 15L //minutes
        private const val PERIOD = 1L //day

        fun makeRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            //OneTimeWorkRequestBuilder<>()
            return PeriodicWorkRequestBuilder<WallpaperWorker>(PERIOD, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(INITIAL_DELAY, TimeUnit.MINUTES)
                .build()
        }
    }
}