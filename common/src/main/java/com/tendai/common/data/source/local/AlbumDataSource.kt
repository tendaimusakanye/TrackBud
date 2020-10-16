package com.tendai.common.data.source.local

import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Album

class AlbumDataSource : DataSource.Albums {

    override suspend fun getAlbums(limit: Int): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumsForArtist(artistId: Long): List<Album> {
        TODO("Not yet implemented")
    }
}

//testing version control