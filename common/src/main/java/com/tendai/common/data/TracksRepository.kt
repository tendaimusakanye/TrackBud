package com.tendai.common.data

import com.tendai.common.data.model.Track
import com.tendai.common.data.source.local.TracksDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TracksRepository(private val tracksDataSource: TracksDataSource) : Repository.Tracks {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun getTracks(): List<Track>? {
        var tracks: List<Track>? = null
        return if (tracks.isNullOrEmpty()) {
            scope.launch {
                tracks = tracksDataSource.getTracks()
            }
            tracks
        } else {
            tracks
        }
    }

    override fun getTracksForArtist(artistId: Long): List<Track>? {
        var tracks: List<Track>? = null
        return if (tracks.isNullOrEmpty()) {
            scope.launch {
                tracks = tracksDataSource.getTracksForArtist(artistId)
            }
            tracks
        } else {
            tracks
        }
    }

    override fun getTracksForAlbums(albumId: Long): List<Track>? {
        var tracks: List<Track>? = null
        return if (tracks.isNullOrEmpty()) {
            scope.launch {
                tracks = tracksDataSource.getTracksForAlbum(albumId)
            }
            tracks
        } else {
            tracks
        }
    }

    override fun getTracksForPlaylist(playlistId: Long): List<Track>? {
        var tracks: List<Track>? = null
        return if (tracks.isNullOrEmpty()) {
            scope.launch {
                tracks = tracksDataSource.getTracksForPlaylist(playlistId)
            }
            tracks
        } else {
            tracks
        }
    }

}