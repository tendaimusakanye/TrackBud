package com.tendai.musicx.ui.tracks.di

import com.tendai.musicx.ui.tracks.TracksFragment
import dagger.Subcomponent

@Subcomponent(modules = [TracksModule::class])
interface TracksComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): TracksComponent
    }

    fun inject(fragment: TracksFragment)
}