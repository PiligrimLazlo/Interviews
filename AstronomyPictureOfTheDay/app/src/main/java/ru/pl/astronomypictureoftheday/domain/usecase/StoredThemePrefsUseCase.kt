package ru.pl.astronomypictureoftheday.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesRepository
import javax.inject.Inject

class StoredThemePrefsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    val storedTheme: Flow<Int> = preferencesRepository.storedTheme

    suspend fun setTheme(theme: Int) {
        preferencesRepository.setTheme(theme)
    }

}