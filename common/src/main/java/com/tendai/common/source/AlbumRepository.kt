package com.tendai.common.source

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.di.ServiceModule
import com.tendai.common.extensions.*
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Album
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val albumLocalDataSource: LocalDataSource.Albums,
    @ServiceModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
        albums.map {
            MediaMetadataCompat.Builder().apply {
                id = "${it.id}"
                album = it.albumTitle
                albumArtist = it.albumArtist
                year = it.yearReleased
                trackCount = it.numberOfTracks
                albumArtUri = "${it.albumArtUri}"
                flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE

                //for ease of displaying
                displayTitle = it.albumTitle
                displaySubtitle = it.albumArtist
                displayDescription = "${it.numberOfTracks} tracks"
                displayIconUri = "${it.albumArtUri}"
            }.build()
        }
}