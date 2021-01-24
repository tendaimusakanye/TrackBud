package com.tendai.common.media.source

import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource

class TracksRepository(private val tracksLocalDataSource: LocalDataSource.Tracks) : Repository,
    Repository.Tracks {

    override fun getTrackDetails(trackId: Int): MediaMetadataCompat =
        retrieveMediaItemDetails(trackId) { tracksLocalDataSource.getTrackDetails(trackId) }

    override fun getTracks(): List<MediaMetadataCompat> =
        retrieveMediaItemList() { tracksLocalDataSource.getTracks() }

    override fun getTracksForArtist(artistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(
            artistId
        ) { tracksLocalDataSource.getTracksForArtist(artistId) }

    override fun getTracksForAlbum(albumId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(albumId) { tracksLocalDataSource.getTracksInAlbum(albumId) }

    override fun getTracksForPlaylist(playlistId: Int): List<MediaMetadataCompat> =
        retrieveMediaItemList(
            playlistId
        ) { tracksLocalDataSource.getTracksInPlaylist(playlistId) }
}

// TODO( I think getTracks Works Just confirm from the test in the previous commits)