package com.tendai.common.data

import com.tendai.common.data.model.Playlist

class PlaylistRepository : DataSource.PlaylistSource {
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