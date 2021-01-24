package com.tendai.common.media.source

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.extensions.mapFrom

interface Repository {

    interface Tracks {

        fun getTrackDetails(trackId: Int): MediaMetadataCompat

        fun getTracks(): List<MediaMetadataCompat>

        fun getTracksForArtist(artistId: Int): List<MediaMetadataCompat>

        fun getTracksForAlbum(albumId: Int): List<MediaMetadataCompat>

        fun getTracksForPlaylist(playlistId: Int): List<MediaMetadataCompat>

    }

    interface Albums {
        fun getAlbums(limit: Int): List<MediaMetadataCompat>

        fun getAlbumsForArtist(artistId: Int): List<MediaMetadataCompat>

        fun getAlbum(albumId: Int): MediaMetadataCompat

    }

    interface Artists {
        fun getAllArtists(): List<MediaMetadataCompat>
    }

    interface Playlists {

        fun getAllPlaylists(limit: Int): List<MediaMetadataCompat>

        suspend fun createNewPlaylist(name: String): Uri?

        suspend fun deletePlaylist(playlistId: Int): Int

        suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int

        suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int

    }
}


/**
 * Util methods for the repositories
 */
internal inline fun <reified T> retrieveMediaItemDetails(
    int: Int,
    block: (int: Int) -> T
): MediaMetadataCompat {
    var mediaItem: T?
    var metadataCompat: MediaMetadataCompat? = null

    return metadataCompat ?: run {
        //try catch should be thrown here if this throws and exception but it doesn't
        // when the coroutine gets here it jumps out of this method and does other things. The code below is only
        //executed when the coroutine returns.
        mediaItem = block.invoke(int)
        metadataCompat = MediaMetadataCompat.Builder()
            .mapFrom(mediaItem!!)
            .build()

        metadataCompat
    }!!
}

/**
 * if mediaItemsList is null, Null is never returned only an empty list since no results matching the given criteria
 * where found. If null is returned then something drastic happened.
 */
internal inline fun <reified T> retrieveMediaItemList(
    int: Int = -1,
    block: (int: Int) -> List<T>
): List<MediaMetadataCompat> {

    var mediaItemsList: List<T>
    var metadataCompats: List<MediaMetadataCompat>? = null

    return metadataCompats ?: run {
        mediaItemsList = block.invoke(int)
        mediaItemsList!!.let { list ->
            if (list.isNotEmpty()) {
                val result = list.map { item ->
                    MediaMetadataCompat.Builder()
                        .mapFrom(item)
                        .build()
                }
                metadataCompats = result
            }
        }
        metadataCompats
    } ?: listOf()
}

//todo: check if list of media metadata is empty or not before using it.


