package ru.pl.astronomypictureoftheday.view.photodetails

import android.app.Application
import android.app.WallpaperManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.FileOutputStream
import java.net.URL

private const val TAG = "PhotoDetailsViewModel"

class PhotoDetailsViewModel(
    private val application: Application,
    private val photo: FavouritePhoto
) : ViewModel() {
    private val _detailsState: MutableStateFlow<PhotoDetailsState> =
        MutableStateFlow(PhotoDetailsState())
    val detailsState: StateFlow<PhotoDetailsState>
        get() = _detailsState.asStateFlow()

    private lateinit var bitmap: Bitmap


    fun saveImageToInternalFolder() {
        viewModelScope.launch(Dispatchers.IO) {

            _detailsState.update { it.copy(isSavingPhoto = true) }

            val filePath = ImageManager.getImageFullPathFile(photo.title)
            loadPhoto()

            if (!filePath.exists())
                FileOutputStream(filePath).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
                }

            _detailsState.update {
                it.copy(isSavingPhoto = false)
            }
        }
    }

    private suspend fun downloadBitmapFromNet(): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val urlEntity = URL(photo.imageHdUrl)
            val inputStream = urlEntity.openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext bitmap
    }

    private suspend fun loadPhoto() {
        val file = ImageManager.getImageFullPathFile(photo.title)
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.absolutePath)
        } else if (!::bitmap.isInitialized) {
            bitmap = downloadBitmapFromNet() ?: throw IllegalArgumentException()
        }
    }

    //todo add dialog with 3 choices: 1)homescreen 2)lockscreen 3)cancel
    fun setWallpapers() {
        viewModelScope.launch(Dispatchers.IO) {
            _detailsState.update { it.copy(isSettingWallpaper = true) }

            val wallpaperManager = WallpaperManager.getInstance(application)
            loadPhoto()
            val rect = scaleBitmap()
            wallpaperManager.setBitmap(bitmap, rect, false)

            _detailsState.update {
                it.copy(isSettingWallpaper = false)
            }
        }
    }

    private fun scaleBitmap(): Rect {
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
        return Rect(start.x, start.y, end.x, end.y)
    }
}


data class PhotoDetailsState(
    val isSavingPhoto: Boolean = false,
    val isSettingWallpaper: Boolean = false,
)