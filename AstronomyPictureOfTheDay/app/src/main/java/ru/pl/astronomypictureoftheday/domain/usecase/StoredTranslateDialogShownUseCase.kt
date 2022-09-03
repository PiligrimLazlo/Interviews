package ru.pl.astronomypictureoftheday.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesRepository
import javax.inject.Inject

class StoredTranslateDialogShownUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    val storedTranslateDialogShown: Flow<Boolean> =
        preferencesRepository.storedTranslateDialogAlreadyShown

    suspend fun setTranslateDialogShown(isCancelled: Boolean) {
        preferencesRepository.setTranslateDialogAlreadyShown(isCancelled)
    }

}