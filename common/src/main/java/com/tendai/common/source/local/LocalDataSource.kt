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

        fun getTrackDetails(trackId: Long): Track

        fun getTracks(): List<Track>

        fun getTracksByArtist(artistId: Long): List<Track>

        fun getTracksInAlbum(albumId: Long): List<Track>

        fun getTracksInPlaylist(playlistId: Long): List<Track>
    }

    interface Albums {
        fun getAlbums(limit: Int): List<Album>

        fun getAlbumsByArtist(artistId: Long): List<Album>

        fun getAlbumDetails(albumId: Long): Album
    }

    interface Artists {
        fun getAllArtists(): List<Artist>
    }

    interface Playlists {

        fun getAllPlaylists(limit: Int): List<Playlist>

        fun createNewPlaylist(name: String): Uri?

        fun deletePlaylist(playlistId: Int): Int

        fun addTracksToPlaylist(playlistId: Long, trackIds: LongArray): Int

        fun removeTrackFromPlaylist(trackIds: LongArray): Int
    }

    /**
     * @param selection is similar to sql's WHERE clause e.g WHERE name = "Tendai"
     * @param selectionArgs is similar to the "Tendai" string above except it should be an array when working with
     * content resolvers.
     * Method is for convenience of default and named arguments.
     */
    fun createCursor(
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




