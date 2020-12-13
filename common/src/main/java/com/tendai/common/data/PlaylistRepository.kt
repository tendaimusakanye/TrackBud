package com.tendai.common.data

import android.net.Uri
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.source.local.PlaylistDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class PlaylistRepository(private val playlistDataSource: PlaylistDataSource) :
    DataSource.Playlists {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override suspend fun getAllPlaylists(limit: Int): List<Playlist> =
        retrieveMediaItemList(limit, scope) { playlistDataSource.getAllPlaylists(limit) }

    override suspend fun createNewPlaylist(name: String): Uri? {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(playlistId: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int {
        TODO("Not yet implemented")
    }

    override suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int {
        TODO("Not yet implemented")
    }

    override fun getNumberOfSongsInPlaylist(playlistId: Int): Int {
        TODO("Not yet implemented")
    }

}