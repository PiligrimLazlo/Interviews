package ru.pl.astronomypictureoftheday.view.favouriteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.model.PhotoEntity
import ru.pl.astronomypictureoftheday.model.repositories.DbPhotoRepository

class FavouritesViewModel : ViewModel() {

    //todo передавать в конструкторе
    private val dbPhotoRepository = DbPhotoRepository.get()

    private val _photosEntity: MutableStateFlow<List<PhotoEntity>> =
        MutableStateFlow(emptyList())
    val photosEntity: StateFlow<List<PhotoEntity>>
        get() = _photosEntity.asStateFlow()

    init {
        viewModelScope.launch {
            dbPhotoRepository.getPhotos().collect {
                //_favouritePhotos.value = it
                val list = it
                _photosEntity.update { list }
            }
        }
    }

}