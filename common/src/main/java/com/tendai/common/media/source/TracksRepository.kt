package com.tendai.common.media.source

import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource

class TracksRepository(private val tracksLocalDataSource: LocalDataSource.Tracks) : Repository,
    Repository.Tracks {

    override fun getTrackDetails(trackId: Int): MediaMetadataCompat =
        retrieveMediaItemMetadata(trackId) { tracksLocalDataSource.getTrackDetails(trackId) }

    override fun getTracks(): List<MediaMetadataCompat> =
        retrieveMediaItemMetadataList() { tracksLocalDataSource.getTracks() }

    override fun getTracksByArtist(artistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemMetadataList(
            artistId
        ) { tracksLocalDataSource.getTracksForArtist(artistId) }

    override fun getTracksByAlbum(albumId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemMetadataList(albumId) { tracksLocalDataSource.getTracksInAlbum(albumId) }

    override fun getTracksByPlaylist(playlistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemMetadataList(
            playlistId
        ) { tracksLocalDataSource.getTracksInPlaylist(playlistId) }
}

// TODO( I think getTracks Works Just confirm from the test in the previous commits)