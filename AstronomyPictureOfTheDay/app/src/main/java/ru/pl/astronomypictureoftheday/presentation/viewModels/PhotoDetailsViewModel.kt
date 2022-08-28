package ru.pl.astronomypictureoftheday.presentation.viewModels

import android.app.Application
import android.app.WallpaperManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.utils.ImageManager
import javax.inject.Inject


class PhotoDetailsViewModel @Inject constructor(
    private val application: Application,
    private val photo: PhotoEntity,
    private val imageManager: ImageManager
) : ViewModel() {
    private val _detailsState: MutableStateFlow<PhotoDetailsState> =
        MutableStateFlow(PhotoDetailsState())
    val detailsState: StateFlow<PhotoDetailsState>
        get() = _detailsState.asStateFlow()


    fun saveImageToPictureFolder() {
        viewModelScope.launch(Dispatchers.IO) {
            _detailsState.update { it.copy(isSavingPhoto = true) }

            val filePath = imageManager.getPublicImageFullPathFile(photo.title)
            val saved = imageManager.savePhoto(photo.imageHdUrl, filePath)

            _detailsState.update { it.copy(isSavingPhoto = false) }
            if (saved != null) {
                _detailsState.update {
                    it.copy(userMessage = application.getString(R.string.successfully_saved_picture))
                }
            } else {
                _detailsState.update {
                    it.copy(userMessage = application.getString(R.string.connection_error))
                }
            }


        }
    }


    fun setWallpapers(choicePosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _detailsState.update { it.copy(isSettingWallpaper = true) }

            val wallpaperManager = WallpaperManager.getInstance(application)
            val filePath = imageManager.getPublicImageFullPathFile(photo.title)
            val bitmap = imageManager.loadPhotoFromCache(filePath)
                ?: imageManager.loadBitmapFromNet(photo.imageHdUrl)

            val flag = when (choicePosition) {
                0 -> WallpaperManager.FLAG_SYSTEM
                1 -> WallpaperManager.FLAG_LOCK
                2 -> WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
                else -> WallpaperManager.FLAG_SYSTEM
            }

            if (bitmap != null) {
                val scaledImageData = imageManager.scaleBitmapForWallpapers(bitmap)
                wallpaperManager.setBitmap(
                    scaledImageData.bitmap,
                    scaledImageData.rect,
                    false,
                    flag
                )
            }

            _detailsState.update { it.copy(isSettingWallpaper = false) }
            if (bitmap != null) {
                _detailsState.update {
                    it.copy(userMessage = application.getString(R.string.wallpapers_set))
                }
            } else {
                _detailsState.update {
                    it.copy(userMessage = application.getString(R.string.connection_error))
                }
            }

        }
    }

    fun userMessageShown() {
        _detailsState.update { currentUiState ->
            currentUiState.copy(userMessage = null)
        }
    }

}


data class PhotoDetailsState(
    val isSavingPhoto: Boolean = false,
    val isSettingWallpaper: Boolean = false,
    val userMessage: String? = null
)