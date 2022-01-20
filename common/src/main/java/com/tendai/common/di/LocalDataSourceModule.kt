package com.tendai.common.di

import com.tendai.common.source.local.*
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class LocalDataSourceModule {
    @Singleton
    @Binds
    abstract fun bindAlbumDataSource(albumDataSource: LocalAlbumDataSource): LocalDataSource.Albums

    @Singleton
    @Binds
    abstract fun bindTracksDataSource(tracksDataSource: LocalTracksDataSource): LocalDataSource.Tracks

    @Singleton
    @Binds
    abstract fun bindPlaylistDataSource(playlistDataSource: LocalPlaylistDataSource): LocalDataSource.Playlists

    @Singleton
    @Binds
    abstract fun bindArtistDataSource(artistDataSource: LocalArtistDataSource): LocalDataSource.Artists
}