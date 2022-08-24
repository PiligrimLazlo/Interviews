package ru.pl.astronomypictureoftheday.view.photolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.repositories.PreferencesRepository.Companion.THEME_LIGHT
import ru.pl.astronomypictureoftheday.model.PhotoEntity
import ru.pl.astronomypictureoftheday.model.repositories.NetPhotoRepository
import ru.pl.astronomypictureoftheday.model.repositories.DbPhotoRepository
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.File

private const val TAG = "PhotoListViewModel";

class PhotoListViewModel : ViewModel() {
    //todo передавать в конструкторе
    private val netPhotoRepository = NetPhotoRepository.get()
    private val dbPhotoRepository = DbPhotoRepository.get()


    val photoEntityItemsFromPaging: Flow<PagingData<PhotoEntity>>
    private var listFavPhotos = listOf<PhotoEntity>()

    private val imageManager: ImageManager = ImageManager()

    init {
        //собираем сохр избранные фотки из репозитория БД
        collectSavedPhotos()
        //при первой загрузке подменяем айтемы
        photoEntityItemsFromPaging = collectInfPhotoList()
    }

    private fun collectInfPhotoList(): Flow<PagingData<PhotoEntity>> {
        return netPhotoRepository
            .fetchPhotos()
            .map {
                it.map { oldFavPhoto ->
                    val newFavPhoto =
                        listFavPhotos.find { favouritePhoto ->
                            favouritePhoto.title == oldFavPhoto.title
                        }
                    newFavPhoto ?: oldFavPhoto
                }
            }
            .cachedIn(viewModelScope)
    }

    private fun collectSavedPhotos() {
        viewModelScope.launch {
            dbPhotoRepository.getPhotos().collectLatest { newFavPhotoList ->
                listFavPhotos = newFavPhotoList
            }
        }
    }

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