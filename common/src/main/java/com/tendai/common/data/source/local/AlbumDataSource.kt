package com.tendai.common.data.source.local

import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Album

class AlbumDataSource : DataSource.AlbumSource {

    override suspend fun getAlbums(): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumsForArtist(artistId: Long): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlbums(vararg album: Album) {
        TODO("Not yet implemented")
    }
}