package com.tendai.common.source

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat

interface Repository {
    interface Tracks {

        suspend fun getTrackDetails(trackId: Long): MediaMetadataCompat

        suspend fun getTracks(): List<MediaMetadataCompat>

        suspend fun getTracksForArtist(artistId: Long): List<MediaMetadataCompat>

        suspend fun getTracksInAlbum(albumId: Long): List<MediaMetadataCompat>

        suspend fun getTracksInPlaylist(playlistId: Long): List<MediaMetadataCompat>

    }

    interface Albums {
        suspend fun getAlbums(limit: Int): List<MediaMetadataCompat>

        suspend fun getAlbumsByArtist(artistId: Long): List<MediaMetadataCompat>

        suspend fun getAlbum(albumId: Long): MediaMetadataCompat

    }

    interface Artists {
        suspend fun getAllArtists(): List<MediaMetadataCompat>
    }

    interface Playlists {

        suspend fun getAllPlaylists(limit: Int): List<MediaMetadataCompat>

        suspend fun createNewPlaylist(name: String): Uri?

        suspend fun deletePlaylist(playlistId: Int): Int

        suspend fun addTracksToPlaylist(playlistId: Long, trackIds: LongArray): Int

        suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int

    }

}





