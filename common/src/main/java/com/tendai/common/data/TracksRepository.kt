package com.tendai.common.data

import com.tendai.common.data.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class TracksRepository(private val tracksDataSource: DataSource.Tracks) : DataSource.Tracks {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override suspend fun getTrackDetails(trackId: Int): Track =
        retrieveMediaItemDetails(trackId, scope) { tracksDataSource.getTrackDetails(trackId) }

    override suspend fun getTracks(): List<Track> =
        retrieveMediaItemList(scope = scope) { tracksDataSource.getTracks() }

    override suspend fun getTracksForArtist(artistId: Int): List<Track> =
        retrieveMediaItemList(artistId, scope) { tracksDataSource.getTracksForArtist(artistId) }

    override suspend fun getTracksForAlbum(albumId: Int): List<Track> =
        retrieveMediaItemList(albumId, scope) { tracksDataSource.getTracksForAlbum(albumId) }

    override suspend fun getTracksForPlaylist(playlistId: Int): List<Track> =
        retrieveMediaItemList(
            playlistId,
            scope
        ) { tracksDataSource.getTracksForPlaylist(playlistId) }
}
// TODO: Check if getTracks()works since it has no int parameter