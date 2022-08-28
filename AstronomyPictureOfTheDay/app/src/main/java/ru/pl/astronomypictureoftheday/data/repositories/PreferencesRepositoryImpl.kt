package ru.pl.astronomypictureoftheday.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import ru.pl.astronomypictureoftheday.domain.repository.PreferencesRepository
import java.lang.IllegalStateException

class PreferencesRepositoryImpl private constructor(
    private val dataStore: DataStore<Preferences>
): PreferencesRepository {

    override val storedTheme: Flow<Int> = dataStore.data.map {
        it[THEME_KEY] ?: 0
    }.distinctUntilChanged()

    override suspend fun setTheme(theme: Int) {
        dataStore.edit {
            it[THEME_KEY] = theme
        }
    }

    override val storedAutoWallpEnabled: Flow<Boolean> = dataStore.data.map {
        it[AUTO_WALLP_KEY] ?: false
    }.distinctUntilChanged()

    override suspend fun setAutoWallpEnabled(isEnabled: Boolean) {
        dataStore.edit {
            it[AUTO_WALLP_KEY] = isEnabled
        }
    }

    companion object {
        private val THEME_KEY = intPreferencesKey("theme_key")
        private val AUTO_WALLP_KEY = booleanPreferencesKey("wallp_key")

        private var INSTANCE: PreferencesRepositoryImpl? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }
                INSTANCE = PreferencesRepositoryImpl(dataStore)
            }
        }

        fun get(): PreferencesRepositoryImpl =
            INSTANCE ?: throw IllegalStateException("Preferences repository must be initialized")
    }

}