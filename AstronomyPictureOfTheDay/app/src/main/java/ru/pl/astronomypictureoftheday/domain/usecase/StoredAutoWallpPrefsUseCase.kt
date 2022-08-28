package ru.pl.astronomypictureoftheday.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesRepository
import javax.inject.Inject

class StoredAutoWallpPrefsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    val isAutoWallp: Flow<Boolean> = preferencesRepository.storedAutoWallpEnabled

    suspend fun setAutoWallp(boolean: Boolean) {
        preferencesRepository.setAutoWallpEnabled(boolean)
    }

}