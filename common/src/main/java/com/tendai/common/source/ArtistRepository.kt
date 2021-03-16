package com.tendai.common.source

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.extensions.*
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtistRepository(private val artistLocalDataSource: LocalDataSource.Artists) :
    Repository.Artists {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun getAllArtists(): List<MediaMetadataCompat> = withContext(ioDispatcher) {
        val artists =
            retrieveMediaItemsList() { artistLocalDataSource.getAllArtists() }
        return@withContext createMetadata(artists)
    }

    private fun createMetadata(artists: List<Artist>): List<MediaMetadataCompat> =
        artists.map { artist ->
            MediaMetadataCompat.Builder().apply {
                id = artist.artistId.toString()
                this.artist = artist.artistName
                trackCount = artist.numberOfTracks.toLong()
                flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                displayTitle = artist.artistName
                displayDescription =
                    "${artist.numberOfAlbums} albums|${artist.numberOfTracks} tracks"
            }.build()
        }
}
//TODO("Handle a display icon for artist i.e. random , from albums")