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
            val albums = retrieveMediaItemsList{
                albumLocalDataSource.getAlbums(limit)
            }
            return@withContext createMetadata(albums)
        }

    override suspend fun getAlbumsByArtist(artistId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val albumsByArtist =
                retrieveMediaItemsList {
                    albumLocalDataSource.getAlbumsForArtist(
                        artistId
                    )
                }
            return@withContext createMetadata(albumsByArtist)
        }

    override suspend fun getAlbum(albumId: Long): MediaMetadataCompat = withContext(ioDispatcher) {
        val albumDetails = retrieveMediaItem() {
            albumLocalDataSource.getAlbum(albumId)
        }
        return@withContext createMetadata(listOf(albumDetails))[0]
    }

    private fun createMetadata(albums: List<Album>): List<MediaMetadataCompat> =
        albums.map {
            MediaMetadataCompat.Builder().apply {
                id = it.id.toString()
                album = it.albumTitle
                albumArtist = it.albumArtist
                year = it.yearReleased.toLong()
                trackCount = it.numberOfTracks.toLong()
                albumArtUri = it.albumArtUri.toString()
                flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE

                //for ease of displaying
                displayTitle = it.albumTitle
                displaySubtitle = it.albumArtist
                displayDescription = "${it.numberOfTracks} tracks"
                displayIconUri = it.albumArtUri.toString()
            }.build()
        }
}