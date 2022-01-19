package com.tendai.common.source.local

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import com.tendai.common.source.model.Album
import com.tendai.common.source.model.Artist
import com.tendai.common.source.model.Playlist
import com.tendai.common.source.model.Track

/**
 *  Interface to Data Layer. Concrete DataSources implement this interface as an abstraction
 */
interface LocalDataSource {
    interface Tracks {
        suspend fun getTrackDetails(trackId: Long): Track

        suspend fun getTracks(): List<Track>

        suspend fun getTracksByArtist(artistId: Long): List<Track>

        suspend fun getTracksInAlbum(albumId: Long): List<Track>

        suspend fun getTracksInPlaylist(playlistId: Long): List<Track>

    }

    interface Albums {
        suspend fun getAlbums(limit: Int): List<Album>

        suspend fun getAlbumsByArtist(artistId: Long): List<Album>

        suspend fun getAlbumDetails(albumId: Long): Album
    }

    interface Artists {
        suspend fun getAllArtists(): List<Artist>
    }

    interface Playlists {

        suspend fun getAllPlaylists(limit: Int): List<Playlist>

        suspend fun createNewPlaylist(name: String): Uri?

        suspend fun deletePlaylist(playlistId: Int): Int

        suspend fun addTracksToPlaylist(playlistId: Long, trackIds: LongArray): Int

        suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int
    }

    /**
     * @param selection is similar to sql's WHERE clause e.g WHERE name = "Tendai"
     * @param selectionArgs is similar to the "Tendai" string above except it should be an array when working with
     * content resolvers.
     * Method is for convenience of default and named arguments.
     */
    fun getCursor(
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
}

fun getAlbumArtUri(albumId: Long): Uri {
    val uri = Uri.parse(ALBUM_ART_PATH)
    return ContentUris.withAppendedId(uri, albumId)
}
private const val ALBUM_ART_PATH = "content://media/external/audio/albumart"




