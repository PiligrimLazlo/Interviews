package ru.pl.astronomypictureoftheday.view.photolist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.PreferencesRepository
import ru.pl.astronomypictureoftheday.model.PreferencesRepository.Companion.THEME_LIGHT
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.model.api.NasaPhotoRepository
import ru.pl.astronomypictureoftheday.model.room.FavouritePhotoRepository

private const val TAG = "PhotoListViewModel";

class PhotoListViewModel : ViewModel() {
    //todo передавать в конструкторе
    private val preferencesRepository = PreferencesRepository.get()
    private val netPhotoRepository = NasaPhotoRepository()
    private val dbPhotoRepository = FavouritePhotoRepository.get()

    private val _uiState: MutableStateFlow<PhotoListUiState> = MutableStateFlow(PhotoListUiState())
    val uiState: StateFlow<PhotoListUiState>
        get() = _uiState.asStateFlow()

    val favouritePhotoItemsFromPaging: Flow<PagingData<FavouritePhoto>>
    //val favouritePhotoItemsFromDb: Flow<List<FavouritePhoto>>

    init {
        viewModelScope.launch {
            //собираем тему из репозитория настроек
            preferencesRepository.storedTheme.collectLatest { storedTheme ->
                try {
                    _uiState.update { oldState ->
                        oldState.copy(theme = storedTheme)
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to fetch gallery items", ex)
                }
            }
        }

        viewModelScope.launch {
            //собираем сохр избранные фотки из репозитория БД
            dbPhotoRepository.getFavouritePhotos().collectLatest { newFavPhotoList ->
                _uiState.update {
                    it.copy(favouritePhotosList = newFavPhotoList)
                }
            }
        }

        //при первой загрузке подменяем айтемы
        favouritePhotoItemsFromPaging = netPhotoRepository
            .fetchTopPhotos()
            .map {
                it.map { oldFavPhoto ->
                    val newFavPhoto =
                        uiState.value.favouritePhotosList.find { favouritePhoto ->
                            favouritePhoto.title == oldFavPhoto.title
                        }
                    newFavPhoto ?: oldFavPhoto
                }
            }
            .cachedIn(viewModelScope)
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }

    fun onSaveFavouriteButtonPressed(favouritePhoto: FavouritePhoto) {
        viewModelScope.launch(Dispatchers.IO) {
            //todo localPhotoPath - где-то взять
            if (dbPhotoRepository.getFavouritePhoto(favouritePhoto.title) == null) {
                dbPhotoRepository.addFavouritePhoto(
                    favouritePhoto.copy(
                        isFavourite = true,
                        localPhotoPath = ""
                    )
                )
            } else {
                dbPhotoRepository.deleteFavouritePhoto(favouritePhoto.title)
            }
        }
    }


}

data class PhotoListUiState(
    val theme: Int = THEME_LIGHT,
    val favouritePhotosList: List<FavouritePhoto> = emptyList()
)