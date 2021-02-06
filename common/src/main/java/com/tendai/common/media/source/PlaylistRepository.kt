package com.tendai.common.media.source

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.extensions.*
import com.tendai.common.media.source.local.LocalDataSource
import com.tendai.common.media.source.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepository(private val playlistLocalDataSource: LocalDataSource.Playlists) :
    Repository.Playlists {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun getAllPlaylists(limit: Int): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val playlists =
                retrieveMediaItemsList(limit) { playlistLocalDataSource.getAllPlaylists(limit) }
            return@withContext createMetadata(playlists)
        }

    override suspend fun createNewPlaylist(name: String): Uri? = withContext(ioDispatcher) {
        return@withContext playlistLocalDataSource.createNewPlaylist(name)
    }


    override suspend fun deletePlaylist(playlistId: Int): Int = withContext(ioDispatcher) {
        return@withContext playlistLocalDataSource.deletePlaylist(playlistId)
    }


    override suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int =
        withContext(ioDispatcher) {
            return@withContext playlistLocalDataSource.addTracksToPlaylist(playlistId, trackIds)
        }


    override suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int =
        withContext(ioDispatcher) {
            return@withContext playlistLocalDataSource.removeTrackFromPlaylist(trackIds)
        }

    private fun createMetadata(playlists: List<Playlist>): List<MediaMetadataCompat> =
        playlists.map { playlist ->
            MediaMetadataCompat.Builder().apply {
                id = playlist.playlistId.toString()
                title = playlist.playlistName
                trackCount = playlist.numberOfTracks.toLong()
                flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                displayTitle = playlist.playlistName
                displayDescription = playlist.numberOfTracks.toString()
            }.build()
        }

}