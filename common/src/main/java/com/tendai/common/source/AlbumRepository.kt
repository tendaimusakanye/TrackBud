package com.tendai.common.source

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.extensions.*
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Album
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AlbumRepository(
    private val albumLocalDataSource: LocalDataSource.Albums,
    private val ioDispatcher: CoroutineDispatcher
) : Repository.Albums {

    override suspend fun getAlbums(limit: Int): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val albums = albumLocalDataSource.getAlbums(limit)

            return@withContext createMetadata(albums)
        }

    override suspend fun getAlbumsByArtist(artistId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val albumsByArtist = albumLocalDataSource.getAlbumsByArtist(artistId)

            return@withContext createMetadata(albumsByArtist)
        }

    override suspend fun getAlbum(albumId: Long): MediaMetadataCompat = withContext(ioDispatcher) {
        val albumDetails = albumLocalDataSource.getAlbumDetails(albumId)

        return@withContext createMetadata(listOf(albumDetails))[0]
    }

    private fun createMetadata(albums: List<Album>): List<MediaMetadataCompat> =
        albums.map { album ->
            MediaMetadataCompat.Builder().apply {
                id = album.id.toString()
                this.album = album.albumTitle
                albumArtist = album.albumArtist
                year = album.yearReleased.toLong()
                trackCount = album.numberOfTracks.toLong()
                albumArtUri = album.albumArtUri.toString()
                flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE

                //for ease of displaying
                displayTitle = album.albumTitle
                displaySubtitle = album.albumArtist
                displayDescription = "${album.numberOfTracks} tracks"
                displayIconUri = album.albumArtUri.toString()
            }.build()
        }
}