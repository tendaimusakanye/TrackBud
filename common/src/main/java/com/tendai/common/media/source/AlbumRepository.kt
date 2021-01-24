package com.tendai.common.media.source

import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AlbumRepository(private val albumLocalDataSource: LocalDataSource.Albums) : Repository,
    Repository.Albums {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getAlbums(limit: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(limit, scope) { albumLocalDataSource.getAlbums(limit) }

    override fun getAlbumsForArtist(artistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(artistId, scope) { albumLocalDataSource.getAlbumsForArtist(artistId) }

    override fun getAlbum(albumId: Int): MediaMetadataCompat =
        retrieveMediaItemDetails(albumId, scope) { albumLocalDataSource.getAlbum(albumId) }
}