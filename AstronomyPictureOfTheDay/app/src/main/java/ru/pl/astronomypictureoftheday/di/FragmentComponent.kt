package ru.pl.astronomypictureoftheday.di

import dagger.BindsInstance
import dagger.Subcomponent
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.presentation.fragments.PhotoDetailsFragment

@Subcomponent(modules = [ViewModelDetailsModule::class])
interface FragmentComponent {


    fun inject(photoDetailsFragment: PhotoDetailsFragment)

    @Subcomponent.Factory
    interface FragmentComponentFactory {

        fun create(
            @BindsInstance detailPhotoEntity: PhotoEntity
        ): FragmentComponent

    }
}