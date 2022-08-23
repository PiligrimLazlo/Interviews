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
import ru.pl.astronomypictureoftheday.R
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

    private val imageManager: ImageManager = ImageManager()



    fun saveImageToPictureFolder() {
        viewModelScope.launch(Dispatchers.IO) {
            _detailsState.update { it.copy(isSavingPhoto = true) }

            val filePath = imageManager.getPublicImageFullPathFile(photo.title)
            imageManager.savePhoto(photo.imageHdUrl, filePath)

            _detailsState.update { it.copy(isSavingPhoto = false) }
        }
    }


    //todo add dialog with 3 choices: 1)homescreen 2)lockscreen 3)cancel
    fun setWallpapers() {
        viewModelScope.launch(Dispatchers.IO) {
            _detailsState.update { it.copy(isSettingWallpaper = true) }

            val wallpaperManager = WallpaperManager.getInstance(application)
            val filePath = imageManager.getPublicImageFullPathFile(photo.title)
            val bitmap = imageManager.loadPhotoFromCache(filePath) ?:
                imageManager.loadBitmapFromNet(photo.imageHdUrl)
            if (bitmap != null) {
                val rect = imageManager.scaleBitmapForWallpapers(bitmap)
                wallpaperManager.setBitmap(bitmap, rect, false)
            }

            _detailsState.update {
                it.copy(isSettingWallpaper = false)
            }
        }
    }

}


data class PhotoDetailsState(
    val isSavingPhoto: Boolean = false,
    val isSettingWallpaper: Boolean = false,
)