package com.tendai.common.data

import com.tendai.common.data.model.Track

class TracksRepository : DataSource.Tracks {
    override suspend fun getTracks(): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksForArtist(artistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksForAlbums(albumId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

}