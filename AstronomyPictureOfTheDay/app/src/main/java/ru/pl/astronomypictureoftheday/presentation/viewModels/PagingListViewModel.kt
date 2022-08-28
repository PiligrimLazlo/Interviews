package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.usecase.*
import ru.pl.astronomypictureoftheday.utils.ImageManager
import javax.inject.Inject

class PagingListViewModel @Inject constructor(
    imageManager: ImageManager,
    getPhotoDbUseCase: GetPhotoDbUseCase,
    addPhotoDbUseCase: AddPhotoDbUseCase,
    deletePhotoDbUseCase: DeletePhotoDbUseCase,
    getPhotosDbUseCase: GetPhotosDbUseCase,
    private val fetchPhotosNetUseCase: FetchPhotosNetUseCase
) : ListParentViewModel(
    imageManager,
    getPhotoDbUseCase,
    addPhotoDbUseCase,
    deletePhotoDbUseCase,
    getPhotosDbUseCase
) {
    //это поле наблюдается из фрагмента
    private val _dbPhotoListState = MutableStateFlow<List<PhotoEntity>>(emptyList())
    val dbPhotoListState: StateFlow<List<PhotoEntity>>
        get() = _dbPhotoListState.asStateFlow()

    //это поле наблюдается из фрагмента
    val photoEntityItemsFromPaging: Flow<PagingData<PhotoEntity>>

    init {
        //собираем сохр избранные фотки из репозитория БД
        collectSavedPhotos()
        //при первой загрузке подменяем айтемы
        photoEntityItemsFromPaging = collectInfPhotoList()
    }

    private fun collectInfPhotoList(): Flow<PagingData<PhotoEntity>> {
        return fetchPhotosNetUseCase()
            /*.map {
                it.map { oldFavPhoto ->
                    val newFavPhoto =
                        listFavPhotos.find { favouritePhoto ->
                            favouritePhoto.title == oldFavPhoto.title
                        }
                    newFavPhoto ?: oldFavPhoto
                }
            }*/
            .cachedIn(viewModelScope)
    }


    private fun collectSavedPhotos() {
        viewModelScope.launch {
            getPhotosDbUseCase().collectLatest { newFavPhotoList ->
                _dbPhotoListState.update { newFavPhotoList }
            }
        }
    }

}