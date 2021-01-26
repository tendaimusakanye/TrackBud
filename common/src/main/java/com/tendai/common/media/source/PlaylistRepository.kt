package com.tendai.common.media.source

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource

class PlaylistRepository(private val playlistLocalDataSource: LocalDataSource.Playlists) :
    Repository,
    Repository.Playlists {

    override fun getAllPlaylists(limit: Int): List<MediaMetadataCompat> =
        retrieveMediaItemMetadataList(limit) { playlistLocalDataSource.getAllPlaylists(limit) }

    override suspend fun createNewPlaylist(name: String): Uri? =
        playlistLocalDataSource.createNewPlaylist(name)

    override suspend fun deletePlaylist(playlistId: Int): Int =
        playlistLocalDataSource.deletePlaylist(playlistId)

    override suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int =
        playlistLocalDataSource.addTracksToPlaylist(playlistId, trackIds)

    override suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int =
        playlistLocalDataSource.removeTrackFromPlaylist(trackIds)
}