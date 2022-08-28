package ru.pl.astronomypictureoftheday.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.pl.astronomypictureoftheday.presentation.TopPhotoApplication
import ru.pl.astronomypictureoftheday.presentation.fragments.FavouritesListFragment
import ru.pl.astronomypictureoftheday.presentation.fragments.PagingListFragment
import ru.pl.astronomypictureoftheday.presentation.fragments.TabsFragment

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun fragmentComponentFactory(): FragmentComponent.FragmentComponentFactory

    fun inject(favouritesListFragment: FavouritesListFragment)

    fun inject(pagingListFragment: PagingListFragment)

    fun inject(tabsFragment: TabsFragment)

    fun inject(application: TopPhotoApplication)

    @Component.Factory
    interface ApplicationComponentFactory {

        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent

    }

}