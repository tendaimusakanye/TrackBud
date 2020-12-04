package com.tendai.common.data

import com.tendai.common.data.model.Album
import com.tendai.common.data.source.local.AlbumDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlbumsRepository(private val albumDataSource: AlbumDataSource) : Repository.Albums {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun getAlbums(limit: Int): List<Album>? {
        var albumList: List<Album>? = null
        return if (albumList == null) {
            scope.launch {
                albumList = albumDataSource.getAlbums(limit) ?: listOf()
            }
            albumList
        } else {
            albumList
        }
    }

    override fun getAlbumsForArtist(artistId: Int): List<Album>? {
        var albums: List<Album>? = null
        return if (albums == null) {
            scope.launch {
                albums = albumDataSource.getAlbumsForArtist(artistId) ?: listOf()
            }
            albums
        } else {
            albums
        }

    }

    override fun getAlbum(id: Int): Album? {
        var album: Album? = null
        return if (album == null) {
            scope.launch {
                album = albumDataSource.getAlbum(id)
            }
            album
        } else {
            album
        }
    }

}