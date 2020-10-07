package com.tendai.common.data.source.local

import com.tendai.common.data.model.Artist
import com.tendai.common.data.DataSource

class ArtistDataSource : DataSource.ArtistSource {
    override suspend fun getArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

    override suspend fun insertArtist(vararg artist: Artist) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArtist(vararg artist: Artist) {
        TODO("Not yet implemented")
    }

    override suspend fun updateNumberOfTracksForArtist(numberOfTracks: Int, artistId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateNumberOfAlbumsForArtist(numberOfAlbums: Int, artistId: Int) {
        TODO("Not yet implemented")
    }

}