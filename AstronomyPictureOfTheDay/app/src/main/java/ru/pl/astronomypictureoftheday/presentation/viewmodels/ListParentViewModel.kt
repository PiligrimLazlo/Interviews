package ru.pl.astronomypictureoftheday.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.usecase.AddPhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.DeletePhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.GetPhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.GetPhotosDbUseCase
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.File
import javax.inject.Inject

open class ListParentViewModel @Inject constructor(
    private val imageManager: ImageManager,
    private val getPhotoDbUseCase: GetPhotoDbUseCase,
    private val addPhotoDbUseCase: AddPhotoDbUseCase,
    private val deletePhotoDbUseCase: DeletePhotoDbUseCase,
    protected val getPhotosDbUseCase: GetPhotosDbUseCase
) : ViewModel() {

    fun onSaveFavouriteButtonPressed(photo: PhotoEntity, filesDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            //сохраняем запись в базу и фото в кэш
            val filePath = imageManager.getInternalImageFullPathFile(photo.title, filesDir)
            if (getPhotoDbUseCase(photo.title) == null) {
                addPhotoDbUseCase(
                    photo.copy(
                        isFavourite = true,
                        cachePhotoPath = filePath.absolutePath
                    )
                )
                imageManager.savePhoto(photo.imageUrl, filePath)
            } else {
                deletePhotoDbUseCase(photo.title)
                imageManager.deletePhoto(filePath)
            }
        }
    }


}