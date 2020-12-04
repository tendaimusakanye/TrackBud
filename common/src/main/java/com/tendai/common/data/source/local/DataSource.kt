package com.tendai.common.data.source.local

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import com.tendai.common.data.model.Album
import com.tendai.common.data.model.Artist
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.model.Track

/**
 * Interface to Data Layer. Concrete Data Sources implement this interface as an abstraction
 */
interface DataSource {

    interface Tracks {

        suspend fun getTracks(): List<Track>?

        suspend fun getTracksForArtist(artistId: Long): List<Track>?

        suspend fun getTracksForAlbum(albumId: Long): List<Track>?

        suspend fun getTracksForPlaylist(playlistId: Long): List<Track>?

    }

    interface Albums {
         suspend fun getAlbums(limit: Int): List<Album>?

         suspend fun getAlbumsForArtist(artistId: Int): List<Album>?

         suspend fun getAlbum(albumId: Int): Album?


    }

    interface Artists {
   fun getAllArtists(): List<Artist>?
    }

    interface Playlists {

        suspend fun getAllPlaylists(limit: Int): List<Playlist>?

        suspend fun createNewPlaylist(name: String): Uri?

        suspend fun deletePlaylist(playlistId: Int): Int

        suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int

        suspend fun removeTrackFromPlaylist(trackId: Long): Int

        fun getNumberOfSongsInPlaylist(playlistId: Int): Int
    }

}

/**
 * @param selection is similar to sql's WHERE clause e.g WHERE name = "Tendai"
 * @param selectionArgs is similar to the "Tendai" string above except it should be an array when working with
 * content resolvers.
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
