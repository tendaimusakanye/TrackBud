package com.tendai.common.data.source.local

import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.model.Track

class PlaylistDataSource : DataSource.Playlists{
    override suspend fun getAllPlaylists(limit: Int): List<Playlist> {
        TODO("Not yet implemented")
    }

    override suspend fun createPlaylist(playlist: Playlist?) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeTrackFromPlaylist(track: Track) {
        TODO("Not yet implemented")
    }

}