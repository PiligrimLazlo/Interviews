package ru.pl.astronomypictureoftheday.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import ru.pl.astronomypictureoftheday.presentation.viewModels.*

@Module
interface ViewModelDetailsModule {


    @Binds
    @IntoMap
    @ViewModelKey(PhotoDetailsViewModel::class)
    fun bindPhotoDetailsViewModel(photoDetailsViewModel: PhotoDetailsViewModel): ViewModel


}