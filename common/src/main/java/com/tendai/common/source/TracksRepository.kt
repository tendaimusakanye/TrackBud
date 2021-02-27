package com.tendai.common.source

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.extensions.*
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class TracksRepository(private val tracksLocalDataSource: LocalDataSource.Tracks) :
    Repository.Tracks {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun getTrackDetails(trackId: Int): MediaMetadataCompat =
        withContext(ioDispatcher) {
            val trackMetadata =
                retrieveMediaItem(trackId) {
                    tracksLocalDataSource.getTrackDetails(
                        trackId
                    )
                }
            return@withContext createMetadata(listOf(trackMetadata))[0]
        }

    override suspend fun getTracks(): List<MediaMetadataCompat> = withContext(ioDispatcher) {
        val tracks =
            retrieveMediaItemsList { tracksLocalDataSource.getTracks() }
        return@withContext createMetadata(tracks)
    }

    override suspend fun getTracksForArtist(artistId: Int): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val trackByArtist =
                retrieveMediaItemsList(artistId) {
                    tracksLocalDataSource.getTracksByArtist(
                        artistId
                    )
                }
            return@withContext createMetadata(trackByArtist)
        }

    override suspend fun getTracksInAlbum(albumId: Int): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val tracksInAlbum =
                retrieveMediaItemsList(albumId) {
                    tracksLocalDataSource.getTracksInAlbum(
                        albumId
                    )
                }
            return@withContext createMetadata(tracksInAlbum)
        }

    override suspend fun getTracksInPlaylist(playlistId: Int): List<MediaMetadataCompat> =
        withContext(ioDispatcher) {
            val tracksInPlaylist =
                retrieveMediaItemsList(playlistId) {
                    tracksLocalDataSource.getTracksInPlaylist(playlistId)
                }
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
                trackNumber = track.trackNumber.toString()
                albumArtUri = track.albumArtUri.toString()
                flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                displayTitle = track.trackName
                displaySubtitle = track.artistName
                displayDescription = track.albumName
                displayIconUri = track.albumArtUri.toString()
            }.build()
        }
    }
}

// TODO( I think getTracks Works Just confirm from the test in the previous commits)
//TODO : Fix the hacky way of getting the trackDetailsMetadata