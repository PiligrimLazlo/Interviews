package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.data.repositories.NetPhotoRepositoryImpl

private const val TAG = "PhotoListViewModel";

class PhotoListViewModel : ListParentViewModel() {
    //todo передавать в конструкторе
    private val netPhotoRepositoryImpl = NetPhotoRepositoryImpl.get()
    //private val dbPhotoRepository = DbPhotoRepository.get()


    //private var listFavPhotos = listOf<PhotoEntity>()
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
        return netPhotoRepositoryImpl
            .fetchPhotos()
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
            dbPhotoRepositoryIml.getPhotos().collectLatest { newFavPhotoList ->
                //listFavPhotos = newFavPhotoList
                _dbPhotoListState.update { newFavPhotoList }
            }
        }
    }

}