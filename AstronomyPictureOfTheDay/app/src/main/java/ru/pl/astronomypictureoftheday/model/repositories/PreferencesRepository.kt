package ru.pl.astronomypictureoftheday.model.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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

    companion object {
        private val THEME_KEY = intPreferencesKey("theme_key")
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1

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