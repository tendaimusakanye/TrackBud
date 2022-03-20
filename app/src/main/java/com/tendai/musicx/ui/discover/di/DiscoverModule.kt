package com.tendai.musicx.ui.discover.di

import androidx.lifecycle.ViewModel
import com.tendai.musicx.di.ViewModelKey
import com.tendai.musicx.ui.discover.DiscoverViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DiscoverModule {

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun bindViewModel(viewModel: DiscoverViewModel): ViewModel
}