package ru.pl.astronomypictureoftheday.presentation.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.data.repositories.PreferencesRepositoryImpl
import ru.pl.astronomypictureoftheday.presentation.viewModels.TabsViewModel.Companion.THEME_LIGHT
import ru.pl.astronomypictureoftheday.workers.WallpaperWorker

private const val TAG = "TabsViewModel";


class TabsViewModel(application: Application) : AndroidViewModel(application) {

    private val _tabsState: MutableStateFlow<TabsUiState> = MutableStateFlow(TabsUiState())
    val tabsState: StateFlow<TabsUiState>
        get() = _tabsState.asStateFlow()

    //todo передавать в конструкторе
    private val preferencesRepositoryImpl = PreferencesRepositoryImpl.get()

    init {
        //собираем тему из репозитория настроек
        collectTheme()
        //собираем работает ли воркер и автоустанавливает обои
        collectIsAutoWallpEnabled()
    }

    private fun collectTheme() {
        viewModelScope.launch {
            preferencesRepositoryImpl.storedTheme.collectLatest { storedTheme ->
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
            preferencesRepositoryImpl.storedAutoWallpEnabled.collectLatest { storedBool ->
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
            preferencesRepositoryImpl.setTheme(theme)
        }
    }

    fun setAutoWallpapersEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            preferencesRepositoryImpl.setAutoWallpEnabled(isEnabled)
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

    companion object {
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
    }
}

data class TabsUiState(
    val theme: Int = THEME_LIGHT,
    val isAutoWallpapersEnabled: Boolean = false
)