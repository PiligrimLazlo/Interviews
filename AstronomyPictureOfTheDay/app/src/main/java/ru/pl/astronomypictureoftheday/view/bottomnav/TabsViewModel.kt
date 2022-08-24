package ru.pl.astronomypictureoftheday.view.bottomnav

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.repositories.PreferencesRepository

private const val TAG = "TabsViewModel";


class TabsViewModel: ViewModel() {

    private val _tabsState: MutableStateFlow<TabsUiState> = MutableStateFlow(TabsUiState())
    val tabsState: StateFlow<TabsUiState>
        get() = _tabsState.asStateFlow()

    private val preferencesRepository = PreferencesRepository.get()

    init {
        //собираем тему из репозитория настроек
        collectTheme()
    }

    private fun collectTheme() {
        viewModelScope.launch {
            preferencesRepository.storedTheme.collectLatest { storedTheme ->
                try {
                    _tabsState.update { oldState ->
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

}

data class TabsUiState(
    val theme: Int = PreferencesRepository.THEME_LIGHT,
)