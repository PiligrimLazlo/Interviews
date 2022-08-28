package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.data.repositories.DbPhotoRepositoryIml
import ru.pl.astronomypictureoftheday.domain.usecase.AddPhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.DeletePhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.GetPhotoDbUseCase
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.File

open class ListParentViewModel: ViewModel() {

    //todo передавать в конструкторе
    protected val dbPhotoRepositoryIml = DbPhotoRepositoryIml.get()

    private val imageManager: ImageManager = ImageManager()
    private val getPhotoDbUseCase: GetPhotoDbUseCase = GetPhotoDbUseCase(dbPhotoRepositoryIml)
    private val addPhotoDbUseCase: AddPhotoDbUseCase = AddPhotoDbUseCase(dbPhotoRepositoryIml)
    private val deletePhotoDbUseCase: DeletePhotoDbUseCase = DeletePhotoDbUseCase(dbPhotoRepositoryIml)


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