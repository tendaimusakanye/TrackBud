package com.tendai.common.source

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.extensions.*
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Track
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class TracksRepository(
    private val tracksLocalDataSource: LocalDataSource.Tracks,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Repository.Tracks {

    override suspend fun getTrackDetails(trackId: Long): MediaMetadataCompat =
        withContext(ioDispatcher) {
            val trackMetadata = tracksLocalDataSource.getTrackDetails(trackId)

            return@withContext createMetadata(listOf(trackMetadata))[0]
        }

    override suspend fun getTracks(): List<MediaMetadataCompat> = withContext(ioDispatcher) {
        val tracks =
            retrieveMediaItemsList { tracksLocalDataSource.getTracks() }
        return@withContext createMetadata(tracks)
    }

    override suspend fun getTracksForArtist(artistId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val trackByArtist =
                retrieveMediaItemsList {
                    tracksLocalDataSource.getTracksByArtist(
                        artistId
                    )
                }
            return@withContext createMetadata(trackByArtist)
        }

    override suspend fun getTracksInAlbum(albumId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val tracksInAlbum =
                retrieveMediaItemsList {
                    tracksLocalDataSource.getTracksInAlbum(
                        albumId
                    )
                }
            return@withContext createMetadata(tracksInAlbum)
        }

    override suspend fun getTracksInPlaylist(playlistId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val tracksInPlaylist =
                retrieveMediaItemsList {
                    tracksLocalDataSource.getTracksInPlaylist(playlistId)
                }
            return@withContext createMetadata(tracksInPlaylist)
        }

    private fun createMetadata(tracks: List<Track>): List<MediaMetadataCompat> {
        return tracks.map {
            val durationMs = TimeUnit.SECONDS.toMillis(it.duration)

            MediaMetadataCompat.Builder().apply {
                id = "${it.id}"
                title = it.trackName
                album = it.albumName
                artist = it.artistName
                duration = durationMs
                trackNumber = it.trackNumber
                albumArtUri = "${it.albumArtUri}"
                flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

                displayTitle = it.trackName
                displayDescription = it.albumName
                displayIconUri = "${it.albumArtUri}"
                // hack is to cater for the setting the queueTitle in the queueManager class
                displaySubtitle = if (it.artistName == "")
                    it.playlistName
                else
                    it.artistName

            }.build()
        }
    }
}

