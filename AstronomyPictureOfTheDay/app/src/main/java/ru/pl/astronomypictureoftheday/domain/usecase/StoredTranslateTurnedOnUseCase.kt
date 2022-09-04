package ru.pl.astronomypictureoftheday.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesRepository
import javax.inject.Inject

class StoredTranslateTurnedOnUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    val storedAutoTranslateEnabled: Flow<Boolean> =
        preferencesRepository.storedTranslationTurnedOn

    suspend fun setAutoTranslateEnabled(isCancelled: Boolean) {
        preferencesRepository.setTranslationTurnedOn(isCancelled)
    }

}