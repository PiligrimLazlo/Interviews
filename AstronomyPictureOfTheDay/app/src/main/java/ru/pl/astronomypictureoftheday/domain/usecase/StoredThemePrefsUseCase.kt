package ru.pl.astronomypictureoftheday.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesPhotoRepository

class StoredThemePrefsUseCase(private val preferencesPhotoRepository: PreferencesPhotoRepository) {

    val storedTheme: Flow<Int> = preferencesPhotoRepository.storedTheme

    suspend fun setTheme(theme: Int) {
        preferencesPhotoRepository.setTheme(theme)
    }

}