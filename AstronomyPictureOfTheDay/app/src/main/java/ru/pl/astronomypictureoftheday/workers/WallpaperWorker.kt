package ru.pl.astronomypictureoftheday.workers

import android.app.WallpaperManager
import android.content.Context
import androidx.work.*
import ru.pl.astronomypictureoftheday.data.repositories.NetPhotoRepositoryImpl
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.util.concurrent.TimeUnit

class WallpaperWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    //todo передавать в конструкторе
    private val imageManager = ImageManager()
    private val netRepository = NetPhotoRepositoryImpl.get()

    override suspend fun doWork(): Result {
        val photo = netRepository.fetchPhoto()

        val wallpaperManager = WallpaperManager.getInstance(context)
        val filePath = imageManager.getPublicImageFullPathFile(photo.title)
        val bitmap = imageManager.loadPhotoFromCache(filePath)
            ?: imageManager.loadBitmapFromNet(photo.imageHdUrl)
            ?: return Result.failure()
        val scaledImageData = imageManager.scaleBitmapForWallpapers(bitmap)
        wallpaperManager.setBitmap(
            scaledImageData.bitmap,
            scaledImageData.rect,
            false,
            WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
        )

        return Result.success()
    }

    companion object {
        const val AUTO_SET_WALLPAPER = "AUTO_SET_WALLPAPER"
        private const val INITIAL_DELAY = 15L //minutes
        private const val PERIOD = 1L //day

        fun makeRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<WallpaperWorker>(PERIOD, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(INITIAL_DELAY, TimeUnit.MINUTES)
                .build()
        }
    }
}