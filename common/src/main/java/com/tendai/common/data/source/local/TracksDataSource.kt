package com.tendai.common.data.source.local

import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Track

class TracksDataSource : DataSource.Tracks {
    // ContentResolver uses app context
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

