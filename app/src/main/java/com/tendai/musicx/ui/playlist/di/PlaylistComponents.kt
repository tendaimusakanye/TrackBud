package com.tendai.musicx.ui.playlist.di

import com.tendai.musicx.ui.playlist.AddToPlaylistFragment
import com.tendai.musicx.ui.playlist.PlaylistDetailsFragment
import dagger.Subcomponent


@Subcomponent(modules = [AddToPlaylistModule::class])
interface AddToPlaylistComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AddToPlaylistComponent
    }

    fun inject(fragment: AddToPlaylistFragment)
}

@Subcomponent(modules = [PlaylistDetailsModule::class])
interface PlaylistDetailsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): PlaylistDetailsComponent
    }

    fun inject(fragment: PlaylistDetailsFragment)
}