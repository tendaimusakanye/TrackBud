package com.tendai.musicx.ui.album.di

import androidx.lifecycle.ViewModel
import com.tendai.musicx.di.ViewModelKey
import com.tendai.musicx.ui.album.AlbumDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AlbumDetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(AlbumDetailsViewModel::class)
    abstract fun bindViewModel(viewModel: AlbumDetailsViewModel): ViewModel
}