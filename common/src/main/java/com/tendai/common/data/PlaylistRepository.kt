package com.tendai.common.data

import android.net.Uri
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.source.local.PlaylistDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistRepository(private val playlistDataSource: PlaylistDataSource) :
    Repository.Playlists {

    private var scope = CoroutineScope(Dispatchers.IO)

    override fun getAllPlaylists(limit: Int): List<Playlist>? {
        var playlists: List<Playlist>? = null

        return if (playlists == null) {
            scope.launch {
                playlists = playlistDataSource.getAllPlaylists(limit)
            }
            playlists
        } else {
            playlists
        }
    }

    override fun createNewPlaylist(name: String): Uri? {
        TODO("Not yet implemented")
    }

    override fun deletePlaylist(playlistId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int {
        TODO("Not yet implemented")
    }

    override fun removeTrackFromPlaylist(trackId: Long): Int {
        TODO("Not yet implemented")
    }

    override fun getNumberOfSongsInPlaylist(playlistId: Int): Int {
        TODO("Not yet implemented")
    }

}