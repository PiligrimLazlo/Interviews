package ru.pl.astronomypictureoftheday.view.photodetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.pl.astronomypictureoftheday.model.PhotoEntity


class ViewModelFactoryDetails(
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
