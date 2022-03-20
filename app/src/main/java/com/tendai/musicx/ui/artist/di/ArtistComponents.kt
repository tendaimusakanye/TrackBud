package com.tendai.musicx.ui.artist.di

import com.tendai.musicx.ui.artist.ArtistAlbumsFragment
import com.tendai.musicx.ui.artist.ArtistFragment
import com.tendai.musicx.ui.artist.ArtistTracksFragment
import dagger.Subcomponent

@Subcomponent(modules = [ArtistModule::class])
interface ArtistComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ArtistComponent
    }

    fun inject(fragment: ArtistFragment)
}

@Subcomponent(modules = [ArtistTracksModule::class])
interface ArtistTracksComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ArtistTracksComponent
    }

    fun inject(fragment: ArtistTracksFragment)
}

@Subcomponent(modules = [ArtistAlbumsModule::class])
interface ArtistAlbumsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ArtistAlbumsComponent
    }

    fun inject(fragment: ArtistAlbumsFragment)
}
