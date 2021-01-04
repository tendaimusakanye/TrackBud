package com.tendai.common.data

import com.tendai.common.data.model.Album
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AlbumsRepository(private val albumDataSource: DataSource.Albums) : DataSource.Albums {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override suspend fun getAlbums(limit: Int): List<Album> =
        retrieveMediaItemList(limit, scope) { albumDataSource.getAlbums(limit) }

    override suspend fun getAlbumsForArtist(artistId: Int): List<Album> =
        retrieveMediaItemList(artistId, scope) { albumDataSource.getAlbumsForArtist(artistId) }

    override suspend fun getAlbum(albumId: Int): Album =
        retrieveMediaItemDetails(albumId, scope) { albumDataSource.getAlbum(albumId) }
}