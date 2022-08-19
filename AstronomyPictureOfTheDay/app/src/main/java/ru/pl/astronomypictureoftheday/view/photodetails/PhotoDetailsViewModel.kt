package ru.pl.astronomypictureoftheday.view.photodetails

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.net.URL

private const val TAG = "PhotoDetailsViewModel"

class PhotoDetailsViewModel : ViewModel() {
    private val _detailsState: MutableStateFlow<PhotoDetailsState> =
        MutableStateFlow(PhotoDetailsState())
    val details: StateFlow<PhotoDetailsState>
        get() = _detailsState.asStateFlow()

    private lateinit var bitmap: Bitmap


    suspend fun saveImageToInternalFolder(url: String, title: String) {
        _detailsState.update { it.copy(isSavingPhoto = true) }

        val filePath = ImageManager.getImageFullPathFile(title)
        if (filePath.exists() && !::bitmap.isInitialized) {
            bitmap = BitmapFactory.decodeFile(filePath.absolutePath)
        } else if (!::bitmap.isInitialized) {
            bitmap = loadBitmapFromUrl(url) ?: throw IllegalArgumentException()
        }

        if (!filePath.exists())
            FileOutputStream(filePath).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
            }
        _detailsState.update { it.copy(isSavingPhoto = false) }
    }

    private suspend fun loadBitmapFromUrl(url: String): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val urlEntity = URL(url)
            val inputStream = urlEntity.openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext bitmap
    }

    //todo переделать
    suspend fun getDataForWallpapers(url: String, title: String): WallpaperSetHelper =
        withContext(Dispatchers.IO) {
            _detailsState.update { it.copy(isSettingWallpaper = true) }
            val filePath = ImageManager.getImageFullPathFile(title)
            if (filePath.exists() && !::bitmap.isInitialized) {
                bitmap = BitmapFactory.decodeFile(filePath.absolutePath)
            } else if (!::bitmap.isInitialized) {
                bitmap = loadBitmapFromUrl(url) ?: throw IllegalArgumentException()
            }

            val wallpaperHeight = Resources.getSystem().displayMetrics.heightPixels
            val wallpaperWidth = Resources.getSystem().displayMetrics.widthPixels

            val widthFactor = wallpaperWidth.toDouble() / bitmap.width
            val heightFactor = wallpaperHeight.toDouble() / bitmap.height
            //scale (grow) bitmap if it smaller than screen
            if (bitmap.width < wallpaperWidth || bitmap.height < wallpaperHeight) {
                if (widthFactor > heightFactor) {
                    val newBitmapHeight = (bitmap.height * widthFactor).toInt()
                    bitmap = bitmap.scale(wallpaperWidth, newBitmapHeight, false)
                } else {
                    val newBitmapWidth = (bitmap.width * heightFactor).toInt()
                    bitmap = bitmap.scale(newBitmapWidth, wallpaperHeight, false)
                }
            }
            //center cropping big image
            val start = Point(0, 0)
            val end = Point(bitmap.width, bitmap.height)

            if (bitmap.width > wallpaperWidth) {
                start.x = (bitmap.width - wallpaperWidth) / 2
                end.x = start.x + wallpaperWidth
            }
            if (bitmap.height > wallpaperHeight) {
                start.y = (bitmap.height - wallpaperHeight) / 2
                end.y = start.y + wallpaperHeight
            }
            return@withContext WallpaperSetHelper(bitmap, Rect(start.x, start.y, end.x, end.y))
        }

    fun updateStateWallpapersSet() {
        _detailsState.update { it.copy(isSettingWallpaper = false) }
    }
}

//использую для установки обоев
data class WallpaperSetHelper(
    val bitmap: Bitmap,
    val rect: Rect
)

data class PhotoDetailsState(
    val isSavingPhoto: Boolean = false,
    val isSettingWallpaper: Boolean = false,
)