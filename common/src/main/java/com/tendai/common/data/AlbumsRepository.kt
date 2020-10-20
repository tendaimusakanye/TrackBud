package com.tendai.common.data

import com.tendai.common.data.model.Album

class AlbumsRepository :DataSource.Albums {
    override suspend fun getAlbums(limit: Int): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumsForArtist(artistId: Int): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbum(id: Int): Album {
        TODO("Not yet implemented")
    }

}