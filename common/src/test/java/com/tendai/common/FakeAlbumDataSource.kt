package com.tendai.common

import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Album

abstract class FakeAlbumDataSource : LocalDataSource.Albums {
    override fun getAlbums(
        limit: Int
    ): List<Album> {

        val albums = mutableListOf<Album>()

        for (i in 0..100000) {
            albums.add(
                Album(
                    i.toLong(),
                    "Album $i",
                    "Artist $i",
                    i,
                    2 + i,
                    i,
                    null
                )
            )
        }

        return albums

    }

    override fun getAlbum(albumId: Long): Album {
        return Album()
    }
}