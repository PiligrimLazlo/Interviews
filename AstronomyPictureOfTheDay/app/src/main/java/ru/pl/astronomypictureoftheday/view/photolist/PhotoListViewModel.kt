package ru.pl.astronomypictureoftheday.view.photolist

import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.PreferencesRepository
import ru.pl.astronomypictureoftheday.model.PreferencesRepository.Companion.THEME_LIGHT
import ru.pl.astronomypictureoftheday.model.TopPhotoEntity
import ru.pl.astronomypictureoftheday.model.api.NasaPhotoRepository

private const val TAG = "PhotoListViewModel";

class PhotoListViewModel : ViewModel() {
    private val preferencesRepository = PreferencesRepository.get()
    private val nasaPhotoRepository = NasaPhotoRepository()

    private val _uiState: MutableStateFlow<PhotoUiState> = MutableStateFlow(PhotoUiState())
    val uiState: StateFlow<PhotoUiState>
        get() = _uiState.asStateFlow()

    val topPhotoItems: Flow<PagingData<TopPhotoEntity>>

    init {
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

        topPhotoItems = nasaPhotoRepository.fetchTopPhotos().cachedIn(viewModelScope)
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }
}

data class PhotoUiState(
    val theme: Int = THEME_LIGHT
)