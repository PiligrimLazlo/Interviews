package ru.pl.astronomypictureoftheday.workers

import android.app.WallpaperManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.pl.astronomypictureoftheday.model.repositories.NetPhotoRepository
import ru.pl.astronomypictureoftheday.utils.ImageManager

class WallpaperWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val imageManager = ImageManager()
    private val netRepository = NetPhotoRepository.get()

    override suspend fun doWork(): Result {
        val photo = netRepository.fetchPhoto()

        val wallpaperManager = WallpaperManager.getInstance(context)
        val filePath = imageManager.getPublicImageFullPathFile(photo.title)
        val bitmap = imageManager.loadPhotoFromCache(filePath)
            ?: imageManager.loadBitmapFromNet(photo.imageHdUrl)
            ?: return Result.failure()
        val rect = imageManager.scaleBitmapForWallpapers(bitmap)
        wallpaperManager.setBitmap(bitmap, rect, false)

        return Result.success()
    }
}