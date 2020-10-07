package com.tendai.common.data.source.local

import com.tendai.common.data.model.Playlist
import com.tendai.common.data.DataSource

class PlaylistDataSource : DataSource.PlaylistSource{
    override suspend fun getPlaylists(): List<Playlist> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun updateNumberOfPlaylistTracks(numberOfTracks: Int, playlistId: Long) {
        TODO("Not yet implemented")
    }

}