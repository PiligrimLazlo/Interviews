package ru.pl.astronomypictureoftheday.view

import android.util.Log
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

class PhotoListViewModel : ViewModel() {
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
    }

    private fun getPeriod(offset: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT+3")
        val endDate = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, -offset + 1)
        val startDate = sdf.format((calendar.time))

        return Pair(startDate, endDate)
    }
}