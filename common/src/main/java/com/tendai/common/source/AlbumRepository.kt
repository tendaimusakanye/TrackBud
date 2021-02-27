package com.tendai.common.source

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.extensions.*
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlbumRepository(private val albumLocalDataSource: LocalDataSource.Albums) :
    Repository.Albums {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun getAlbums(limit: Int): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val albums = retrieveMediaItemsList(limit) {
                albumLocalDataSource.getAlbums(limit)
            }
            return@withContext createMetadata(albums)
        }

    override suspend fun getAlbumsByArtist(artistId: Int): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val albumsByArtist =
                retrieveMediaItemsList(artistId) {
                    albumLocalDataSource.getAlbumsForArtist(
                        artistId
                    )
                }
            return@withContext createMetadata(albumsByArtist)
        }

    override suspend fun getAlbum(albumId: Int): MediaMetadataCompat = withContext(ioDispatcher) {
        val albumDetails = retrieveMediaItem(albumId) {
            albumLocalDataSource.getAlbum(albumId)
        }
        return@withContext createMetadata(listOf(albumDetails))[0]
    }

    private fun createMetadata(albums: List<Album>): List<MediaMetadataCompat> =
        albums.map { album ->
            MediaMetadataCompat.Builder().apply {
                id = album.id.toString()
                this.album = album.albumTitle
                this.albumArtist = album.albumArtist
                year = album.yearReleased.toLong()
                trackCount = album.numberOfTracks.toLong()
                this.albumArtUri = album.albumArtUri.toString()
                flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE

                //for ease of displaying
                displayTitle = album.albumTitle
                displaySubtitle = album.albumArtist
                displayDescription = "${album.numberOfTracks} tracks"
                displayIconUri = album.albumArtUri.toString()
            }.build()
        }
}