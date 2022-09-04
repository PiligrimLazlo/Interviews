package ru.pl.astronomypictureoftheday.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val fetchPhotosNetUseCase: FetchPhotosNetUseCase,
    private val fetchRangePhotosNetUseCase: FetchRangePhotosNetUseCase
) : ListParentViewModel(
    imageManager,
    getPhotoDbUseCase,
    addPhotoDbUseCase,
    deletePhotoDbUseCase,
    getPhotosDbUseCase
) {
    //это поле наблюдается из фрагмента
    private val _dBPhotosState = MutableStateFlow(DbPhotosState())
    val dBPhotosState: StateFlow<DbPhotosState>
        get() = _dBPhotosState.asStateFlow()

    private val _dateRangeState = MutableStateFlow(DateRangeState())

    //это поле наблюдается из фрагмента (все фото с сегодняшнего)
    val photoEntityItemsFromPaging: Flow<PagingData<PhotoEntity>>

    init {
        //собираем сохр избранные фотки из репозитория БД
        collectSavedPhotos()
        //бесконечный список
        photoEntityItemsFromPaging = collectInfPhotoList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectInfPhotoList(): Flow<PagingData<PhotoEntity>> {
        return _dateRangeState.flatMapLatest {
            if (it.dateRange == null) {
                fetchPhotosNetUseCase()
            } else {
                fetchRangePhotosNetUseCase(it.dateRange.first, it.dateRange.second)
            }
        }.cachedIn(viewModelScope)

//        fetchPhotosNetUseCase()
//            .cachedIn(viewModelScope)
    }

    fun onDateSelected(dateRange: Pair<Long, Long>) {
        //список по выбранному диапазону дат
        _dateRangeState.update { it.copy(dateRange = dateRange) }
    }

    fun onDateSelectedReset() {
        _dateRangeState.update { it.copy(dateRange = null) }
    }


    private fun collectSavedPhotos() {
        viewModelScope.launch {
            getPhotosDbUseCase().collectLatest { newFavPhotoList ->
                _dBPhotosState.update { it.copy(dbPhotoList = newFavPhotoList) }
            }
        }
    }

}

data class DbPhotosState(
    //поле для синхронизации звездочек из БД
    val dbPhotoList: List<PhotoEntity> = emptyList(),
)

data class DateRangeState(
    val dateRange: Pair<Long, Long>? = null
)