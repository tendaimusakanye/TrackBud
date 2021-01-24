package com.tendai.common.media.source

import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource

class AlbumRepository(private val albumLocalDataSource: LocalDataSource.Albums) : Repository,
    Repository.Albums {

    override fun getAlbums(limit: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(limit) { albumLocalDataSource.getAlbums(limit) }

    override fun getAlbumsForArtist(artistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(artistId) { albumLocalDataSource.getAlbumsForArtist(artistId) }

    override fun getAlbum(albumId: Int): MediaMetadataCompat =
        retrieveMediaItemDetails(albumId) { albumLocalDataSource.getAlbum(albumId) }
}