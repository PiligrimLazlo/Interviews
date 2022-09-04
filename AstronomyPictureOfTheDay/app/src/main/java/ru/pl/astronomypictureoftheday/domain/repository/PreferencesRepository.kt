package ru.pl.astronomypictureoftheday.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val storedTheme: Flow<Int>
    suspend fun setTheme(theme: Int)

    val storedAutoWallpEnabled: Flow<Boolean>
    suspend fun setAutoWallpEnabled(isEnabled: Boolean)

    val storedTranslationTurnedOn: Flow<Boolean>
    suspend fun setTranslationTurnedOn(isShown: Boolean)
}