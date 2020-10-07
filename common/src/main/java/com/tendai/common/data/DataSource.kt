package com.tendai.common.data

import com.tendai.common.data.model.Album
import com.tendai.common.data.model.Artist
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.model.Track

/**
 * Interface to Data Layer. The Repositories and Concrete Data Sources implement this interface as an abstraction
 * All these methods work with the app's database.
 * For retrieving media from external storage , see @FileDataSource
 */
interface DataSource {

    interface TrackSource{

        suspend fun getTracks(): List<Track>

        suspend fun getTracksForArtist(artistId: Long): List<Track>

        suspend fun getTracksForAlbums(albumId: Long): List<Track>

        suspend fun getTracksForPlaylist(playlistId: Long): List<Track>

        suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)

        suspend fun deleteTrack(vararg track: Track)

        suspend fun insertTracks(vararg track: Track)

        //    suspend fun insertTrack(track: Track)
    }

    interface AlbumSource {
        suspend fun getAlbums(): List<Album>

        suspend fun getAlbumsForArtist(artistId: Long): List<Album>

        suspend fun insertAlbums(vararg album: Album)
    }

    interface ArtistSource {
        suspend fun getArtists(): List<Artist>

        suspend fun insertArtist(vararg artist: Artist)

        suspend fun deleteArtist(vararg artist: Artist)

        suspend fun updateNumberOfTracksForArtist(numberOfTracks: Int, artistId: Int)

        suspend fun updateNumberOfAlbumsForArtist(numberOfAlbums: Int, artistId: Int)
    }

    interface PlaylistSource {
        suspend fun getPlaylists(): List<Playlist>

        suspend fun insertPlaylist(playlist: Playlist)

        suspend fun deletePlaylist(playlist: Playlist)

        suspend fun updateNumberOfPlaylistTracks(numberOfTracks: Int, playlistId: Long)
    }

}