package ru.pl.astronomypictureoftheday.presentation.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.pl.astronomypictureoftheday.domain.PhotoEntity


class ViewModelFactory(
    private val application: Application,
    private val photo: PhotoEntity
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoDetailsViewModel::class.java)) {
            return PhotoDetailsViewModel(application, photo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
