package com.tendai.musicx.ui.tracks.di

import androidx.lifecycle.ViewModel
import com.tendai.musicx.di.ViewModelKey
import com.tendai.musicx.ui.tracks.TracksViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TracksModule {

    @Binds
    @IntoMap
    @ViewModelKey(TracksViewModel::class)
    abstract fun bindViewModel(viewModel: TracksViewModel): ViewModel
}