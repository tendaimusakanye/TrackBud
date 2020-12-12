package com.tendai.common.data

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import com.tendai.common.data.model.Album
import com.tendai.common.data.model.Artist
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *  Interface to Data Layer. Concrete Repositories and DataSources implement this interface as an abstraction
 */
interface DataSource {
    interface Tracks {

        suspend fun getTrackDetails(trackId: Int): Track

        suspend fun getTracks(): List<Track>

        suspend fun getTracksForArtist(artistId: Int): List<Track>

        suspend fun getTracksForAlbum(albumId: Int): List<Track>

        suspend fun getTracksForPlaylist(playlistId: Int): List<Track>

    }

    interface Albums {
        suspend fun getAlbums(limit: Int): List<Album>

        suspend fun getAlbumsForArtist(artistId: Int): List<Album>

        suspend fun getAlbum(albumId: Int): Album

    }

    interface Artists {
        suspend fun getAllArtists(): List<Artist>
    }

    interface Playlists {

        suspend fun getAllPlaylists(limit: Int): List<Playlist>?

        suspend fun createNewPlaylist(name: String): Uri?

        suspend fun deletePlaylist(playlistId: Int): Int

        suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int

        suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int

        fun getNumberOfSongsInPlaylist(playlistId: Int): Int
    }
}

/**
 * Util methods for the repository class
 */
internal fun <T> retrieveMediaItemDetails(
    int: Int,
    scope: CoroutineScope,
    block: suspend (int: Int) -> T
): T {
    var mediaItem: T? = null
    return mediaItem ?: mediaItem.run {
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
internal fun <T> retrieveMediaItemList(
    int: Int = -1,
    scope: CoroutineScope,
    block: suspend (int: Int) -> List<T>
): List<T> {
    var mediaItemsList: List<T>? = null
    return mediaItemsList ?: mediaItemsList.run {
        scope.launch {
            mediaItemsList = block.invoke(int)
        }
        mediaItemsList
    }!!
}

/**
 * @param selection is similar to sql's WHERE clause e.g WHERE name = "Tendai"
 * @param selectionArgs is similar to the "Tendai" string above except it should be an array when working with
 * content resolvers.
 * Method is for convenience of default and named arguments.
 */
internal fun getCursor(
    contentResolver: ContentResolver,
    uri: Uri,
    projection: Array<out String>? = null,
    selection: String? = null,
    selectionArgs: Array<out String>? = null,
    sortOrder: String? = null
): Cursor? =
    contentResolver.query(
        uri,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )


