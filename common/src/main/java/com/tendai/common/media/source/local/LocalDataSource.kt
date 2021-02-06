package com.tendai.common.media.source.local

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import com.tendai.common.media.source.model.Album
import com.tendai.common.media.source.model.Artist
import com.tendai.common.media.source.model.Playlist
import com.tendai.common.media.source.model.Track

/**
 *  Interface to Data Layer. Concrete DataSources implement this interface as an abstraction
 */
interface LocalDataSource {
    interface Tracks {

        fun getTrackDetails(trackId: Int): Track

        fun getTracks(): List<Track>

        fun getTracksByArtist(artistId: Int): List<Track>

        fun getTracksInAlbum(albumId: Int): List<Track>

        fun getTracksInPlaylist(playlistId: Int): List<Track>

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

        fun createNewPlaylist(name: String): Uri?

        fun deletePlaylist(playlistId: Int): Int

        fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int

        fun removeTrackFromPlaylist(trackIds: LongArray): Int

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

    fun getAlbumArtUri(albumId: Int): Uri {
        val uri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(uri, albumId.toLong())
    }
}

//todo: check the path returned by content provider.ALBUM_ART




