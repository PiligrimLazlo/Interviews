package ru.pl.astronomypictureoftheday.presentation.viewModels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.usecase.AddPhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.DeletePhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.GetPhotoDbUseCase
import ru.pl.astronomypictureoftheday.domain.usecase.GetPhotosDbUseCase
import ru.pl.astronomypictureoftheday.utils.ImageManager
import javax.inject.Inject

class FavouritesListViewModel @Inject constructor(
    imageManager: ImageManager,
    getPhotoDbUseCase: GetPhotoDbUseCase,
    addPhotoDbUseCase: AddPhotoDbUseCase,
    deletePhotoDbUseCase: DeletePhotoDbUseCase,
    getPhotosDbUseCase: GetPhotosDbUseCase
) : ListParentViewModel(
    imageManager,
    getPhotoDbUseCase,
    addPhotoDbUseCase,
    deletePhotoDbUseCase,
    getPhotosDbUseCase
) {

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