package com.tendai.common.data

import com.tendai.common.data.model.Artist

class ArtistRepository: DataSource.ArtistSource {
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