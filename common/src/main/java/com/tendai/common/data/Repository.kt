package com.tendai.common.data

import android.net.Uri
import com.tendai.common.data.model.Album
import com.tendai.common.data.model.Artist
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.model.Track


/**
 *  Interface to Data Layer. Concrete Repositories implement this interface as an abstraction
 */
interface Repository {
    interface Tracks {

         fun getTracks(): List<Track>?

         fun getTracksForArtist(artistId: Long): List<Track>?

         fun getTracksForAlbums(albumId: Long): List<Track>?

         fun getTracksForPlaylist(playlistId: Long): List<Track>?

    }

    interface Albums {
         fun getAlbums(limit: Int): List<Album>?

         fun getAlbumsForArtist(artistId: Int): List<Album>?

        fun getAlbum(id: Int): Album?


    }

    interface Artists {
         fun getAllArtists(): List<Artist>?
    }

    interface Playlists {

         fun getAllPlaylists(limit: Int): List<Playlist>?

         fun createNewPlaylist(name: String): Uri?

         fun deletePlaylist(playlistId: Int): Int

         fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int

         fun removeTrackFromPlaylist(trackId: Long): Int

        fun getNumberOfSongsInPlaylist(playlistId: Int): Int
    }
}