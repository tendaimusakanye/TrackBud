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

internal inline fun <T> retrieveMediaItem(
    block: () -> T
): T {
    var mediaItem: T? = null
    return mediaItem ?: run {
        //try catch should be thrown here if this throws and exception but it doesn't
        // when the coroutine gets here it jumps out of this method and does other things. The code below is only
        //executed when the coroutine returns.
        mediaItem = block.invoke()
        //the mediaItem should never be null it will return fields with empty fields always.
        mediaItem
    }!!
}

//if mediaItemsList is null, Null is never returned only an empty list since no results matching the given criteria
// where found. If null is returned then something drastic happened.
internal inline fun <T> retrieveMediaItemsList(
    block: () -> List<T>
): List<T> {
    var mediaItemsList: List<T>? = null
    return mediaItemsList ?: run {
        mediaItemsList = block.invoke()
        mediaItemsList
    }!!
}

const val PLAYLIST_ICON_URI =
    "android.resource://com.tendai.common.media.source/drawable/ic_playlist"
//todo: check if list of media metadata is empty or not before using it.


