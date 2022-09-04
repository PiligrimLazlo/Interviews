package ru.pl.astronomypictureoftheday.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.pl.astronomypictureoftheday.presentation.viewmodels.*

@Module
interface ViewModelDetailsModule {


    @Binds
    @IntoMap
    @ViewModelKey(PhotoDetailsViewModel::class)
    fun bindPhotoDetailsViewModel(photoDetailsViewModel: PhotoDetailsViewModel): ViewModel


}