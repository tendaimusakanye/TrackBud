package com.tendai.musicx.ui.playlist.di

import androidx.lifecycle.ViewModel
import com.tendai.musicx.di.ViewModelKey
import com.tendai.musicx.ui.playlist.AddToPlaylistViewModel
import com.tendai.musicx.ui.playlist.PlaylistDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class AddToPlaylistModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddToPlaylistViewModel::class)
    abstract fun bindViewModel(viewModel: AddToPlaylistViewModel): ViewModel
}

@Module
abstract class PlaylistDetailsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlaylistDetailsViewModel::class)
    abstract fun bindViewModel(viewModel: PlaylistDetailsViewModel): ViewModel
}