package com.tendai.common.media.source

import android.net.Uri
import com.tendai.common.media.source.model.Album
import com.tendai.common.media.source.model.Artist
import com.tendai.common.media.source.model.Playlist
import com.tendai.common.media.source.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface Repository {

    interface Tracks {

        fun getTrackDetails(trackId: Int): Track

        fun getTracks(): List<Track>

        fun getTracksForArtist(artistId: Int): List<Track>

        fun getTracksForAlbum(albumId: Int): List<Track>

        fun getTracksForPlaylist(playlistId: Int): List<Track>

    }

    interface Albums {
        fun getAlbums(limit: Int): List<Album>

        fun getAlbumsForArtist(artistId: Int): List<Album>

        fun getAlbum(albumId: Int): Album

    }

    interface Artists {
        fun getAllArtists(): List<Artist>
    }

    interface Playlists {

        fun getAllPlaylists(limit: Int): List<Playlist>

        suspend fun createNewPlaylist(name: String): Uri?

        suspend fun deletePlaylist(playlistId: Int): Int

        suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int

        suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int

    }

    /**
     * Util methods for the repositories
     */
    fun <T> retrieveMediaItemDetails(
        int: Int,
        scope: CoroutineScope,
        block: suspend (int: Int) -> T
    ): T {
        var mediaItem: T? = null
        //invoking run on a null object reference ?
        return mediaItem ?: run {
            scope.launch {
                //try catch should be thrown here if this throws and exception but it doesn't
                // when the coroutine gets here it jumps out of this method and does other things. The code below is only
                //executed when the coroutine returns.
                mediaItem = block.invoke(int)
            }
            mediaItem
        }!!
    }

    // invoke the suspend function within the scope.An empty result should usually be returned.
    /**
     * if var1 null is never returned only an empty list since no results matching the given criteria
     * where found. If null is returned then something drastic happened.
     */
    fun <T> retrieveMediaItemList(
        int: Int = -1,
        scope: CoroutineScope,
        block: suspend (int: Int) -> List<T>
    ): List<T> {
        var mediaItemsList: List<T>? = null
        return mediaItemsList ?: run {
            scope.launch {
                mediaItemsList = block.invoke(int)
            }
            mediaItemsList
        }!!
    }
}


