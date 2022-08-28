package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.data.repositories.DbPhotoRepositoryIml
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.File

open class ListParentViewModel: ViewModel() {

    //todo передавать в конструкторе
    protected val dbPhotoRepositoryIml = DbPhotoRepositoryIml.get()
    private val imageManager: ImageManager = ImageManager()

    protected var listFavPhotos = listOf<PhotoEntity>()


    fun onSaveFavouriteButtonPressed(photo: PhotoEntity, filesDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            //сохраняем запись в базу и фото в кэш
            val filePath = imageManager.getInternalImageFullPathFile(photo.title, filesDir)
            if (dbPhotoRepositoryIml.getPhoto(photo.title) == null) {
                dbPhotoRepositoryIml.addPhoto(
                    photo.copy(
                        isFavourite = true,
                        cachePhotoPath = filePath.absolutePath
                    )
                )
                imageManager.savePhoto(photo.imageUrl, filePath)
            } else {
                dbPhotoRepositoryIml.deletePhoto(photo.title)
                imageManager.deletePhoto(filePath)
            }
        }
    }


}