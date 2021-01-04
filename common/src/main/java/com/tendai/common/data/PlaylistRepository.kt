package com.tendai.common.data

import android.net.Uri
import com.tendai.common.data.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class PlaylistRepository(private val playlistDataSource: DataSource.Playlists) :
    DataSource.Playlists {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override suspend fun getAllPlaylists(limit: Int): List<Playlist> =
        retrieveMediaItemList(limit, scope) { playlistDataSource.getAllPlaylists(limit) }

    override suspend fun createNewPlaylist(name: String): Uri? =
        playlistDataSource.createNewPlaylist(name)

    override suspend fun deletePlaylist(playlistId: Int): Int =
        playlistDataSource.deletePlaylist(playlistId)

    override suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int =
        playlistDataSource.addTracksToPlaylist(playlistId, trackIds)

    override suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int =
        playlistDataSource.removeTrackFromPlaylist(trackIds)
}