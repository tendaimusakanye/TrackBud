package com.tendai.common.data

import com.tendai.common.data.model.Album
import com.tendai.common.data.model.Artist
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.model.Track

/**
 * Interface to Data Layer. The Repositories and Concrete Data Sources implement this interface as an abstraction
 */
interface DataSource {

    interface Tracks {

        suspend fun getTracks(): List<Track>

        suspend fun getTracksForArtist(artistId: Long): List<Track>

        suspend fun getTracksForAlbums(albumId: Long): List<Track>

        suspend fun getTracksForPlaylist(playlistId: Long): List<Track>

    }

    interface Albums {
        suspend fun getAlbums(limit: Int): List<Album>

        suspend fun getAlbumsForArtist(artistId: Long): List<Album>

    }

    interface Artists {
        suspend fun getAllArtists(): List<Artist>
    }

    interface Playlists {

        suspend fun getAllPlaylists(limit: Int): List<Playlist>

        suspend fun createPlaylist(playlist: Playlist?)

        suspend fun deletePlaylist(id: Long)

        suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)

        suspend fun removeTrackFromPlaylist(track: Track)

    }
}