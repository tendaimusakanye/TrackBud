package com.tendai.common.media.source.local

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import com.tendai.common.media.source.model.Album
import com.tendai.common.media.source.model.Artist
import com.tendai.common.media.source.model.Playlist
import com.tendai.common.media.source.model.Track

/**
 *  Interface to Data Layer. Concrete Repositories and DataSources implement this interface as an abstraction
 */
interface LocalDataSource {
    interface Tracks {

        suspend fun getTrackDetails(trackId: Int): Track

        suspend fun getTracks(): List<Track>

        suspend fun getTracksForArtist(artistId: Int): List<Track>

        suspend fun getTracksInAlbum(albumId: Int): List<Track>

        suspend fun getTracksInPlaylist(playlistId: Int): List<Track>

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

        suspend fun getAllPlaylists(limit: Int): List<Playlist>

        suspend fun createNewPlaylist(name: String): Uri?

        suspend fun deletePlaylist(playlistId: Int): Int

        suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int

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




