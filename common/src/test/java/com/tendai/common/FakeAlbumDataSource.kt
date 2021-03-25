package com.tendai.common

import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Album

class FakeAlbumDataSource : LocalDataSource.Albums {
    override  fun getAlbums(
        limit: Int
    ): List<Album> {

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

    }

    override  fun getAlbumsForArtist(artistId: Long): List<Album> {
        TODO("Not yet implemented")
    }

    override  fun getAlbum(albumId: Long): Album {
      return Album()
    }
}