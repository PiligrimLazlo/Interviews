package ru.pl.astronomypictureoftheday.view.bottomnav

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.repositories.PreferencesRepository
import ru.pl.astronomypictureoftheday.workers.WallpaperWorker
import java.util.concurrent.TimeUnit

private const val TAG = "TabsViewModel";


class TabsViewModel(application: Application) : AndroidViewModel(application) {

    private val _tabsState: MutableStateFlow<TabsUiState> = MutableStateFlow(TabsUiState())
    val tabsState: StateFlow<TabsUiState>
        get() = _tabsState.asStateFlow()

    private val preferencesRepository = PreferencesRepository.get()

    init {
        //собираем тему из репозитория настроек
        collectTheme()
        //собираем работает ли воркер и автоустанавливает обои
        collectIsAutoWallpEnabled()
    }

    private fun collectTheme() {
        viewModelScope.launch {
            preferencesRepository.storedTheme.collectLatest { storedTheme ->
                try {
                    _tabsState.update { oldState ->
                        oldState.copy(theme = storedTheme)
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to load storedTheme", ex)
                }
            }
        }
    }

    private fun collectIsAutoWallpEnabled() {
        viewModelScope.launch {
            preferencesRepository.storedAutoWallpEnabled.collectLatest { storedBool ->
                try {
                    _tabsState.update { oldState ->
                        oldState.copy(isAutoWallpapersEnabled = storedBool)
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to load isAutoWallpapersEnabled", ex)
                }
            }
        }
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }

    fun setAutoWallpapersEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAutoWallpEnabled(isEnabled)
            notifyWorker(isEnabled)
        }
    }

    private fun notifyWorker(isEnabled: Boolean) {
        if (isEnabled) {
            val periodicRequest = WallpaperWorker.makeRequest()
            WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
                WallpaperWorker.AUTO_SET_WALLPAPER,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        } else {
            WorkManager.getInstance(getApplication())
                .cancelUniqueWork(WallpaperWorker.AUTO_SET_WALLPAPER)
        }
    }


}

data class TabsUiState(
    val theme: Int = PreferencesRepository.THEME_LIGHT,
    val isAutoWallpapersEnabled: Boolean = false
)