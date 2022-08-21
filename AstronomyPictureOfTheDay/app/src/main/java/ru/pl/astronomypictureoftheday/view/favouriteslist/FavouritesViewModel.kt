package ru.pl.astronomypictureoftheday.view.favouriteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.model.repositories.DbPhotoRepository

class FavouritesViewModel : ViewModel() {

    //todo передавать в конструкторе
    private val dbPhotoRepository = DbPhotoRepository.get()

    private val _favouritePhotos: MutableStateFlow<List<FavouritePhoto>> =
        MutableStateFlow(emptyList())
    val favouritePhotos: StateFlow<List<FavouritePhoto>>
        get() = _favouritePhotos.asStateFlow()

    init {
        viewModelScope.launch {
            dbPhotoRepository.getFavouritePhotos().collect {
                //_favouritePhotos.value = it
                val list = it
                _favouritePhotos.update { list }
            }
        }
    }

}