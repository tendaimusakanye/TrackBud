package com.tendai.common.media.source

import com.tendai.common.media.source.model.Album
import com.tendai.common.media.source.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AlbumRepository(private val albumLocalDataSource: LocalDataSource.Albums) : Repository,
    Repository.Albums {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getAlbums(limit: Int): List<Album> =
        retrieveMediaItemList(limit, scope) { albumLocalDataSource.getAlbums(limit) }

    override fun getAlbumsForArtist(artistId: Int): List<Album> =
        retrieveMediaItemList(artistId, scope) { albumLocalDataSource.getAlbumsForArtist(artistId) }

    override fun getAlbum(albumId: Int): Album =
        retrieveMediaItemDetails(albumId, scope) { albumLocalDataSource.getAlbum(albumId) }
}