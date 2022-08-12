package ru.pl.astronomypictureoftheday.view.photodetails

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.Environment
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.net.URL

class PhotoDetailsViewModel : ViewModel() {
    private val _detailsState: MutableStateFlow<PhotoDetailsState> =
        MutableStateFlow(PhotoDetailsState())
    val details: StateFlow<PhotoDetailsState>
        get() = _detailsState.asStateFlow()
    private lateinit var bitmap: Bitmap


    suspend fun saveImageToInternalFolder(url: String) = withContext(Dispatchers.IO) {
        _detailsState.update { it.copy(isSavingPhoto = true) }
        if (!::bitmap.isInitialized) {
            bitmap = loadBitmapFromUrl(url) ?: throw IllegalArgumentException()
        }

        val fileName = "NasaAPOD_" + System.currentTimeMillis() / 1000 + ".png"
        val filePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
        FileOutputStream(filePath).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, it)
        }
        _detailsState.update { it.copy(isSavingPhoto = false) }
    }

    //todo вызывать этот метод при открытии фрагмента и сохранять битмап в поле,
    // чтобы не ждать долго при нажатии кнопки save or setWallpapers
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

    private suspend fun saveImageToInternalFolder(bitmap: Bitmap) = withContext(Dispatchers.IO) {

        /*requireActivity().runOnUiThread {
            toast("$filePath ${getString(R.string.successfully_saved_picture)}")
            changeDownloadState(false)
        }*/
    }

    //todo add dialog with 3 choices: 1)homescreen 2)lockscreen 3)cancel
    suspend fun getDataForWallpapers(url: String): Pair<Bitmap, Rect> =
        withContext(Dispatchers.IO) {
            _detailsState.update { it.copy(isSettingWallpaper = true) }

            if (!::bitmap.isInitialized)
                bitmap = loadBitmapFromUrl(url) ?: throw IllegalArgumentException()

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
            /*requireActivity().runOnUiThread {
                changeSettingWallpapersState(false)
                toast(getString(R.string.done))
            }*/
            return@withContext Pair(bitmap, Rect(start.x, start.y, end.x, end.y))
        }

    fun updateStateWallpapersSet() {
        _detailsState.update { it.copy(isSettingWallpaper = false) }
    }

}

data class PhotoDetailsState(
    val isSavingPhoto: Boolean = false,
    val isSettingWallpaper: Boolean = false
)