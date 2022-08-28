package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.usecase.GetPhotosDbUseCase

class FavouritesListViewModel : ListParentViewModel() {

    private val getPhotosDbUseCase: GetPhotosDbUseCase = GetPhotosDbUseCase(dbPhotoRepositoryIml)


    private val _photosEntity: MutableStateFlow<List<PhotoEntity>> =
        MutableStateFlow(emptyList())
    val photosEntity: StateFlow<List<PhotoEntity>>
        get() = _photosEntity.asStateFlow()

    init {
        viewModelScope.launch {
            getPhotosDbUseCase().collect { newList ->
                _photosEntity.update { newList }
            }
        }
    }

}