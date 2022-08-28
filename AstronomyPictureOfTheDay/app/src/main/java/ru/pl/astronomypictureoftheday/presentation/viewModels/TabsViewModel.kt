package ru.pl.astronomypictureoftheday.presentation.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.usecase.StoredAutoWallpPrefsUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.StoredThemePrefsUseCase
import ru.pl.astronomypictureoftheday.presentation.viewModels.TabsViewModel.Companion.THEME_LIGHT
import ru.pl.astronomypictureoftheday.workers.WallpaperWorker
import javax.inject.Inject

private const val TAG = "TabsViewModel";


class TabsViewModel @Inject constructor(
    private val application: Application,
    private val storedThemePrefsUseCase: StoredThemePrefsUseCase,
    private val storedAutoWallpPrefsUseCase: StoredAutoWallpPrefsUseCase
) : ViewModel() {

    private val _tabsState: MutableStateFlow<TabsUiState> = MutableStateFlow(TabsUiState())
    val tabsState: StateFlow<TabsUiState>
        get() = _tabsState.asStateFlow()


    init {
        //собираем тему из репозитория настроек
        collectTheme()
        //собираем работает ли воркер и автоустанавливает обои
        collectIsAutoWallpEnabled()
    }

    private fun collectTheme() {
        viewModelScope.launch {
            storedThemePrefsUseCase.storedTheme.collectLatest { storedTheme ->
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
            storedAutoWallpPrefsUseCase.isAutoWallp.collectLatest { storedBool ->
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
            storedThemePrefsUseCase.setTheme(theme)
        }
    }

    fun setAutoWallpapersEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            storedAutoWallpPrefsUseCase.setAutoWallp(isEnabled)
            notifyWorker(isEnabled)
        }
    }

    private fun notifyWorker(isEnabled: Boolean) {
        if (isEnabled) {
            val periodicRequest = WallpaperWorker.makeRequest()
            WorkManager.getInstance(application).enqueueUniquePeriodicWork(
                WallpaperWorker.AUTO_SET_WALLPAPER,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        } else {
            WorkManager.getInstance(application)
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