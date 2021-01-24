package com.tendai.common.media.source

import com.tendai.common.media.source.local.LocalDataSource
import com.tendai.common.media.source.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class TracksRepository(private val tracksLocalDataSource: LocalDataSource.Tracks) : Repository,
    Repository.Tracks {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getTrackDetails(trackId: Int): Track =
        retrieveMediaItemDetails(trackId, scope) { tracksLocalDataSource.getTrackDetails(trackId) }

    override fun getTracks(): List<Track> =
        retrieveMediaItemList(scope = scope) { tracksLocalDataSource.getTracks() }

    override fun getTracksForArtist(artistId: Int): List<Track> =
        retrieveMediaItemList(artistId, scope) { tracksLocalDataSource.getTracksForArtist(artistId) }

    override fun getTracksForAlbum(albumId: Int): List<Track> =
        retrieveMediaItemList(albumId, scope) { tracksLocalDataSource.getTracksInAlbum(albumId) }

    override fun getTracksForPlaylist(playlistId: Int): List<Track> =
        retrieveMediaItemList(
            playlistId,
            scope
        ) { tracksLocalDataSource.getTracksInPlaylist(playlistId) }
}

// TODO: Check if getTracks()works since it has no int parameter