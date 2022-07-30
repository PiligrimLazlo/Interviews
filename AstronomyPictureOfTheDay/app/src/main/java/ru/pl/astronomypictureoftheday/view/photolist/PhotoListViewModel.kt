package ru.pl.astronomypictureoftheday.view.photolist

import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.api.NasaPhotoRepository
import ru.pl.astronomypictureoftheday.api.TopPhotoEntity
import ru.pl.astronomypictureoftheday.utils.SERVER_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "PhotoListViewModel";

class PhotoListViewModel(
    private val sharedPrefs: SharedPreferences
) : ViewModel() {
    private val _topPhotos: MutableStateFlow<List<TopPhotoEntity>> = MutableStateFlow(emptyList())
    val topPhotos: StateFlow<List<TopPhotoEntity>>
        get() = _topPhotos.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val datePeriod = getPeriod(10)
                val responseSeveralPhotos =
                    NasaPhotoRepository().fetchTopPhoto(datePeriod.first, datePeriod.second)
                _topPhotos.value = responseSeveralPhotos
            } catch (e: Exception) {
                Log.d(TAG, "Cannot load photo(s)", e)
            }
        }

        initTheme()
    }

    private fun getPeriod(offset: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        //TODO delete
        calendar.add(Calendar.HOUR, -5)

        val sdf = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT+3")
        val endDate = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, -offset + 1)
        val startDate = sdf.format((calendar.time))

        return Pair(startDate, endDate)
    }

    fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }

    private fun initTheme() {
        val savedTheme = getSavedTheme()
        setTheme(
            if (savedTheme == THEME_DARK) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO,
            savedTheme
        )
    }

    private fun saveTheme(theme: Int) = sharedPrefs.edit().putInt(KEY_THEME, theme).apply()
    fun getSavedTheme() = sharedPrefs.getInt(KEY_THEME, THEME_LIGHT)


    companion object {
        const val KEY_THEME = "prefs.theme"
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
    }
}