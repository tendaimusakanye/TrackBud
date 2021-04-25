package com.tendai.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Album

class FakeAlbumDataSource : LocalDataSource.Albums {
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
                    i
                )
            )
        }

        return albums

    }

    override fun getAlbumsByArtist(artistId: Long): List<Album> {
        TODO("Not yet implemented")
    }

    override fun getAlbumDetails(albumId: Long): Album {
        return Album()
    }

    override fun getAlbumArt(albumId: Long): Bitmap {
        return BitmapFactory.decodeFile("")
    }
}