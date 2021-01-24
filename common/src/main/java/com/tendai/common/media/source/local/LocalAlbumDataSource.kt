package com.tendai.common.media.source.local

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.provider.MediaStore.Audio.Albums.*
import android.provider.MediaStore.Audio.Artists.Albums.getContentUri
import android.provider.MediaStore.Audio.Media.ARTIST_ID
import com.tendai.common.media.extensions.mapList
import com.tendai.common.media.source.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI as ALBUMS_URI

class AlbumLocalDataSource(context: Context) : LocalDataSource,
    LocalDataSource.Albums {

    private val ioDispatcher = Dispatchers.IO
    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, ALBUM, ARTIST, ARTIST_ID, FIRST_YEAR, NUMBER_OF_SONGS
    )

    //getAlbums for the discover page
    // Do not use the let scope function on the cursor as it does not close the cursor in case of
    // an error or something else.
    override suspend fun getAlbums(
        limit: Int
    ): List<Album> {
        return withContext(ioDispatcher) {
            val cursor = getCursor(
                contentResolver = contentResolver,
                uri = ALBUMS_URI,
                projection = projection,
                sortOrder = "LIMIT $limit"
            )
            cursor!!.use { result ->
                result.mapList { mapToAlbum(it) }
            }
        }
    }

    // I am using the getCursor method for the flexibility of named arguments.
    // The content resolver's query method does not offer the flexibility named arguments.
    // if the cursor is null then something drastic happened. Let NPE be thrown otherwise we found
    // Nothing. therefore return an empty list or empty mediaItem.
    // are always returned.
    override suspend fun getAlbumsForArtist(artistId: Int): List<Album> {
        return withContext(ioDispatcher) {
            val uri = getContentUri("external", artistId.toLong())
            val cursor = getCursor(
                contentResolver = contentResolver,
                uri = uri,
                projection = projection,
                sortOrder = "$ALBUM ASC"
            )
            cursor!!.use { result ->
                result.mapList { mapToAlbum(it) }
            }
        }
    }

    override suspend fun getAlbum(albumId: Int): Album {
        return withContext(ioDispatcher) {
            val cursor = getCursor(
                contentResolver = contentResolver,
                uri = ALBUMS_URI,
                projection = projection,
                selection = "$_ID = ?",
                selectionArgs = arrayOf(albumId.toString())
            )
            cursor!!.use {
                mapToAlbum(it)
            }
        }
    }


    /**
     * create an album from the cursor.
     * if moveToFirst is zero then no album or albums were found. return an empty list or an empty album
     * which makes sense as the cursor returns an empty list as well.
     *
     * run scope function is better when returning the lambda result and in the presence of object
     * initialization See @link https://kotlinlang.org/docs/reference/scope-functions.html
     */
    private fun mapToAlbum(cursor: Cursor): Album =
        cursor.run {
            Album(
                id = getInt(getColumnIndex(_ID)),
                albumTitle = getString(getColumnIndex(ALBUM)),
                albumArtist = getString(getColumnIndex(ARTIST)),
                artistId = getInt(getColumnIndex(ARTIST_ID)),
                yearReleased = getInt(getColumnIndex(FIRST_YEAR)),
                numberOfTracks = getInt(getColumnIndex(NUMBER_OF_SONGS)),
                albumArtUri = getAlbumArtUri(getInt(getColumnIndex(_ID)))
            )
        }


}
private const val TAG = "LocalAlbumDataSource"

//TODO: check if list is empty or not in-place of error handling list.isEmpty()
