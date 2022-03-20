package com.tendai.musicx.ui.artist.di

import androidx.lifecycle.ViewModel
import com.tendai.musicx.di.ViewModelKey
import com.tendai.musicx.ui.artist.ArtistAlbumsViewModel
import com.tendai.musicx.ui.artist.ArtistTracksViewModel
import com.tendai.musicx.ui.artist.ArtistViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ArtistModule {

    @Binds
    @IntoMap
    @ViewModelKey(ArtistViewModel::class)
    abstract fun bindViewModel(viewModel: ArtistViewModel): ViewModel
}

@Module
abstract class ArtistAlbumsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ArtistAlbumsViewModel::class)
    abstract fun bindViewModel(viewModel: ArtistAlbumsViewModel): ViewModel
}

@Module
abstract class ArtistTracksModule {

    @Binds
    @IntoMap
    @ViewModelKey(ArtistTracksViewModel::class)
    abstract fun bindViewModel(viewModel: ArtistTracksViewModel): ViewModel
}


