package com.tendai.common.media.source

import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class TracksRepository(private val tracksLocalDataSource: LocalDataSource.Tracks) : Repository,
    Repository.Tracks {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getTrackDetails(trackId: Int): MediaMetadataCompat =
        retrieveMediaItemDetails(trackId, scope) { tracksLocalDataSource.getTrackDetails(trackId) }

    override fun getTracks(): List<MediaMetadataCompat> =
        retrieveMediaItemList(scope = scope) { tracksLocalDataSource.getTracks() }

    override fun getTracksForArtist(artistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(
            artistId,
            scope
        ) { tracksLocalDataSource.getTracksForArtist(artistId) }

    override fun getTracksForAlbum(albumId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(albumId, scope) { tracksLocalDataSource.getTracksInAlbum(albumId) }

    override fun getTracksForPlaylist(playlistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(
            playlistId,
            scope
        ) { tracksLocalDataSource.getTracksInPlaylist(playlistId) }
}

// TODO( I think getTracks Works Just confirm from the test in the previous commits)