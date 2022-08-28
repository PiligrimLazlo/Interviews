package ru.pl.astronomypictureoftheday.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesPhotoRepository

class AutoWallpPrefsUseCase(private val preferencesPhotoRepository: PreferencesPhotoRepository) {

    val isAutoWallp: Flow<Boolean> = preferencesPhotoRepository.storedAutoWallpEnabled

    suspend fun setAutoWallp(boolean: Boolean) {
        preferencesPhotoRepository.setAutoWallpEnabled(boolean)
    }

}