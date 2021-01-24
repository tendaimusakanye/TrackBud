package com.tendai.common

import android.content.Context
import com.tendai.common.media.source.*
import com.tendai.common.media.source.local.*

object ServiceLocator {

    private var albumsRepository: Repository.Albums? = null
    private var artistRepository: Repository.Artists? = null
    private var playlistRepository: Repository.Playlists? = null
    private var trackRepository: Repository.Tracks? = null

    fun provideAlbumRepository(context: Context): Repository.Albums =
        albumsRepository ?: createAlbumRepository(createAlbumDataSource(context))

    fun provideArtistRepository(context: Context): Repository.Artists =
        artistRepository ?: createArtistRepository(createArtistDataSource(context))

    fun providePlaylistRepository(context: Context): Repository.Playlists =
        playlistRepository ?: createPlaylistRepository(createPlaylistDataSource(context))

    fun provideTracksRepository(context: Context): Repository.Tracks =
        trackRepository ?: createTrackRepository(createTrackDataSource(context))

    private fun createTrackRepository(trackLocalDataSource: LocalDataSource.Tracks): Repository.Tracks =
        TracksRepository(trackLocalDataSource)

    private fun createTrackDataSource(context: Context): LocalDataSource.Tracks =
        TracksLocalDataSource(context)

    private fun createPlaylistRepository(playlistLocalDataSource: LocalDataSource.Playlists):
            Repository.Playlists {
        return PlaylistRepository(
            playlistLocalDataSource
        )
    }

    private fun createPlaylistDataSource(context: Context): LocalDataSource.Playlists =
        PlaylistLocalDataSource(context)

    private fun createArtistRepository(artistLocalDataSource: LocalDataSource.Artists): Repository.Artists =
        ArtistRepository(artistLocalDataSource)

    private fun createArtistDataSource(context: Context): LocalDataSource.Artists =
        ArtistLocalDataSource(context)

    private fun createAlbumRepository(albumLocalDataSource: LocalDataSource.Albums): Repository.Albums =
        AlbumRepository(albumLocalDataSource)

    private fun createAlbumDataSource(context: Context): LocalDataSource.Albums =
        AlbumLocalDataSource(context)

}