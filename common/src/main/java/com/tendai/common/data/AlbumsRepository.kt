package com.tendai.common.data

import com.tendai.common.data.model.Album

class AlbumsRepository :DataSource.Albums {
    override suspend fun getAlbums(limit: Int): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumsForArtist(artistId: Long): List<Album> {
        TODO("Not yet implemented")
    }

}