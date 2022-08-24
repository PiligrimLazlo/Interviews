package ru.pl.astronomypictureoftheday.model.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.lang.IllegalStateException

class PreferencesRepository private constructor(private val dataStore: DataStore<Preferences>) {

    val storedTheme: Flow<Int> = dataStore.data.map {
        it[THEME_KEY] ?: THEME_LIGHT
    }.distinctUntilChanged()

    suspend fun setTheme(theme: Int) {
        dataStore.edit {
            it[THEME_KEY] = theme
        }
    }

    val storedAutoWallpEnabled: Flow<Boolean> = dataStore.data.map {
        it[AUTO_WALLP_KEY] ?: false
    }.distinctUntilChanged()

    suspend fun setAutoWallpEnabled(isEnabled: Boolean) {
        dataStore.edit {
            it[AUTO_WALLP_KEY] = isEnabled
        }
    }

    companion object {
        private val THEME_KEY = intPreferencesKey("theme_key")
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1

        private val AUTO_WALLP_KEY = booleanPreferencesKey("wallp_key")

        private var INSTANCE: PreferencesRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository =
            INSTANCE ?: throw IllegalStateException("Preferences repository must be initialized")
    }

}