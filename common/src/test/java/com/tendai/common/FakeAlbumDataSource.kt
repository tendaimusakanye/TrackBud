package com.tendai.common

import com.tendai.common.media.source.local.LocalDataSource
import com.tendai.common.media.source.model.Album

class FakeAlbumDataSource : LocalDataSource.Albums {
    override suspend fun getAlbums(
        limit: Int
    ): List<Album> {

//        return withContext(Dispatchers.IO) {
        val albumOne = Album(
            100,
            "Strings and Blings",
            "NastyC",
            1,
            2015,
            12,
            null

        )
        val albumTwo = Album(
            102,
            "Beer bottles and Bongs",
            "Post Malone",
            2,
            2016,
            10,
            null
        )
        return listOf(albumOne, albumTwo)
//        }
    }

    override suspend fun getAlbumsForArtist(artistId: Int): List<Album> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbum(albumId: Int): Album {
        TODO("Not yet implemented")
    }
}