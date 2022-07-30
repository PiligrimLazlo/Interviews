package ru.pl.astronomypictureoftheday.view

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.pl.astronomypictureoftheday.view.photolist.PhotoListViewModel

class ViewModelFactory(private val shredPref: SharedPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoListViewModel::class.java)) {
            return PhotoListViewModel(shredPref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}