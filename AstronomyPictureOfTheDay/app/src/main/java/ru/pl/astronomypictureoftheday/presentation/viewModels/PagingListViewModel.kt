package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.data.repositories.NetPhotoRepositoryImpl

private const val TAG = "PhotoListViewModel";

class PhotoListViewModel : ListParentViewModel() {
    //todo передавать в конструкторе
    private val netPhotoRepositoryImpl = NetPhotoRepositoryImpl.get()
    //private val dbPhotoRepository = DbPhotoRepository.get()


    val photoEntityItemsFromPaging: Flow<PagingData<PhotoEntity>>
    //private var listFavPhotos = listOf<PhotoEntity>()

    //private val imageManager: ImageManager = ImageManager()

    init {
        //собираем сохр избранные фотки из репозитория БД
        collectSavedPhotos()
        //при первой загрузке подменяем айтемы
        photoEntityItemsFromPaging = collectInfPhotoList()
    }

    private fun collectInfPhotoList(): Flow<PagingData<PhotoEntity>> {
        return netPhotoRepositoryImpl
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
            dbPhotoRepositoryIml.getPhotos().collectLatest { newFavPhotoList ->
                listFavPhotos = newFavPhotoList
            }
        }
    }

//    fun onSaveFavouriteButtonPressed(photo: PhotoEntity, filesDir: File) {
//        viewModelScope.launch(Dispatchers.IO) {
//            //сохраняем запись в базу и фото в кэш
//            val filePath = imageManager.getInternalImageFullPathFile(photo.title, filesDir)
//            if (dbPhotoRepository.getPhoto(photo.title) == null) {
//                dbPhotoRepository.addPhoto(
//                    photo.copy(
//                        isFavourite = true,
//                        cachePhotoPath = filePath.absolutePath
//                    )
//                )
//            imageManager.savePhoto(photo.imageUrl, filePath)
//            } else {
//                dbPhotoRepository.deletePhoto(photo.title)
//                imageManager.deletePhoto(filePath)
//            }
//        }
//    }

}