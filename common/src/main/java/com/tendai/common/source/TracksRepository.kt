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
        val tracks = tracksLocalDataSource.getTracks()

        return@withContext createMetadata(tracks)
    }

    override suspend fun getTracksForArtist(artistId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val trackByArtist = tracksLocalDataSource.getTracksByArtist(artistId)

            return@withContext createMetadata(trackByArtist)
        }

    override suspend fun getTracksInAlbum(albumId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val tracksInAlbum = tracksLocalDataSource.getTracksInAlbum(albumId)

            return@withContext createMetadata(tracksInAlbum)
        }

    override suspend fun getTracksInPlaylist(playlistId: Long): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val tracksInPlaylist = tracksLocalDataSource.getTracksInPlaylist(playlistId)

            return@withContext createMetadata(tracksInPlaylist)
        }

    private fun createMetadata(tracks: List<Track>): List<MediaMetadataCompat> {
        return tracks.map { track ->
            val durationMs = TimeUnit.SECONDS.toMillis(track.duration.toLong())
            MediaMetadataCompat.Builder().apply {
                id = track.id.toString()
                title = track.trackName
                album = track.albumName
                artist = track.artistName
                duration = durationMs
                trackNumber = track.trackNumber.toLong()
                albumArtUri = track.albumArtUri.toString()
                flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

                displayTitle = track.trackName
                displayDescription = track.albumName
                displayIconUri = track.albumArtUri.toString()
                // hack is to cater for the setting the queueTitle in the queueManager class
                displaySubtitle = if (track.artistName == "") {
                    track.playlistName
                } else {
                    track.artistName
                }
            }.build()
        }
    }
}

