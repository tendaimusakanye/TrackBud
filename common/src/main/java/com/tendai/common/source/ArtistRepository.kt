package com.tendai.common.source

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.di.ServiceModule
import com.tendai.common.extensions.*
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Artist
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArtistRepository @Inject constructor(
    private val artistLocalDataSource: LocalDataSource.Artists,
    @ServiceModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : Repository.Artists {

    override suspend fun getAllArtists(): List<MediaMetadataCompat> = withContext(ioDispatcher) {
        val artists = artistLocalDataSource.getAllArtists()

        return@withContext createMetadata(artists)
    }

    private fun createMetadata(artists: List<Artist>): List<MediaMetadataCompat> =
        artists.map {
            MediaMetadataCompat.Builder().apply {
                id = "${it.artistId}"
                artist = it.artistName
                trackCount = it.numberOfTracks
                flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE

                displayTitle = it.artistName
                displayDescription = "${it.numberOfAlbums} albums|${it.numberOfTracks} tracks"
            }.build()
        }
}
//TODO("Handle a display icon for artist i.e. random , from albums")
