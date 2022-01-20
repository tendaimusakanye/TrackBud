package com.tendai.common.di

import com.tendai.common.source.*
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTracksRepository(tracksRepository: TracksRepository): Repository.Tracks

    @Singleton
    @Binds
    abstract fun bindAlbumRepository(albumRepository: AlbumRepository): Repository.Albums

    @Singleton
    @Binds
    abstract fun bindArtistRepository(artistRepository: ArtistRepository): Repository.Artists

    @Singleton
    @Binds
    abstract fun bindPlaylistRepository(playlistRepository: PlaylistRepository): Repository.Playlists
}