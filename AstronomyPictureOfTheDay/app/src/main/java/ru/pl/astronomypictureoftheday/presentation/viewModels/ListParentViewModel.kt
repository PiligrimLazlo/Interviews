package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.data.PhotoEntity
import ru.pl.astronomypictureoftheday.data.repositories.DbPhotoRepository
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.File

open class ListParentViewModel: ViewModel() {

    //todo передавать в конструкторе
    protected val dbPhotoRepository = DbPhotoRepository.get()
    private val imageManager: ImageManager = ImageManager()

    protected var listFavPhotos = listOf<PhotoEntity>()


    fun onSaveFavouriteButtonPressed(photo: PhotoEntity, filesDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            //сохраняем запись в базу и фото в кэш
            val filePath = imageManager.getInternalImageFullPathFile(photo.title, filesDir)
            if (dbPhotoRepository.getPhoto(photo.title) == null) {
                dbPhotoRepository.addPhoto(
                    photo.copy(
                        isFavourite = true,
                        cachePhotoPath = filePath.absolutePath
                    )
                )
                imageManager.savePhoto(photo.imageUrl, filePath)
            } else {
                dbPhotoRepository.deletePhoto(photo.title)
                imageManager.deletePhoto(filePath)
            }
        }
    }


}