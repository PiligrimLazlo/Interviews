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
import ru.pl.astronomypictureoftheday.model.repositories.PreferencesRepository
import ru.pl.astronomypictureoftheday.model.repositories.PreferencesRepository.Companion.THEME_LIGHT
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.model.repositories.NetPhotoRepository
import ru.pl.astronomypictureoftheday.model.repositories.DbPhotoRepository
import ru.pl.astronomypictureoftheday.utils.ImageManager
import java.io.File

private const val TAG = "PhotoListViewModel";

class PhotoListViewModel : ViewModel() {
    //todo передавать в конструкторе
    private val preferencesRepository = PreferencesRepository.get()
    private val netPhotoRepository = NetPhotoRepository()
    private val dbPhotoRepository = DbPhotoRepository.get()

    private val _uiState: MutableStateFlow<PhotoListUiState> = MutableStateFlow(PhotoListUiState())
    val uiState: StateFlow<PhotoListUiState>
        get() = _uiState.asStateFlow()

    val favouritePhotoItemsFromPaging: Flow<PagingData<FavouritePhoto>>

    private val imageManager: ImageManager = ImageManager()

    init {
        //собираем тему из репозитория настроек
        collectTheme()
        //собираем сохр избранные фотки из репозитория БД
        collectSavedPhotos()
        //при первой загрузке подменяем айтемы
        favouritePhotoItemsFromPaging = collectInfPhotoList()
    }

    private fun collectInfPhotoList(): Flow<PagingData<FavouritePhoto>> {
        return netPhotoRepository
            .fetchTopPhotos()
            .map {
                it.map { oldFavPhoto ->
                    val newFavPhoto =
                        _uiState.value.favouritePhotosList.find { favouritePhoto ->
                            favouritePhoto.title == oldFavPhoto.title
                        }
                    newFavPhoto ?: oldFavPhoto
                }
            }
            .cachedIn(viewModelScope)
    }

    private fun collectSavedPhotos() {
        viewModelScope.launch {
            dbPhotoRepository.getFavouritePhotos().collectLatest { newFavPhotoList ->
                _uiState.update {
                    it.copy(favouritePhotosList = newFavPhotoList)
                }
            }
        }
    }

    private fun collectTheme() {
        viewModelScope.launch {
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
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }

    fun onSaveFavouriteButtonPressed(photo: FavouritePhoto, filePath: File) {
        viewModelScope.launch(Dispatchers.IO) {
            //todo localPhotoPath - где-то взять
            //сохраняем запись в базу и фото в кэш
            if (dbPhotoRepository.getFavouritePhoto(photo.title) == null) {
                dbPhotoRepository.addFavouritePhoto(
                    photo.copy(
                        isFavourite = true,
                        localPhotoPath = filePath.absolutePath
                    )
                )
            imageManager.savePhoto(photo.imageUrl, filePath)
            } else {
                dbPhotoRepository.deleteFavouritePhoto(photo.title)
                imageManager.deletePhoto(filePath)
            }
        }
    }


}

data class PhotoListUiState(
    val theme: Int = THEME_LIGHT,
    val favouritePhotosList: List<FavouritePhoto> = emptyList()
)