package com.tendai.musicx.ui.album.di

import com.tendai.musicx.ui.album.AlbumDetailsFragment
import dagger.Subcomponent

@Subcomponent(modules = [AlbumDetailsModule::class])
interface AlbumDetailsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AlbumDetailsComponent
    }

    fun inject(fragment: AlbumDetailsFragment)
}