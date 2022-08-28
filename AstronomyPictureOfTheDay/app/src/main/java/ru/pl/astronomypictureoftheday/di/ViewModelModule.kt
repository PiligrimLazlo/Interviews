package ru.pl.astronomypictureoftheday.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import ru.pl.astronomypictureoftheday.presentation.viewModels.*

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FavouritesListViewModel::class)
    fun bindFavouritesListViewModel(favouritesListViewModel: FavouritesListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListParentViewModel::class)
    fun bindListParentViewModel(listParentViewModel: ListParentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PagingListViewModel::class)
    fun bindPagingListViewModel(pagingListViewModel: PagingListViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(TabsViewModel::class)
    fun bindTabsViewModel(tabsViewModel: TabsViewModel): ViewModel

}