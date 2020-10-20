package com.tendai.common.data.source.local

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.provider.MediaStore.Audio.AlbumColumns.*
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
import android.util.Log
import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Album
import com.tendai.common.extensions.mapList

//TODO: Make these data source classes thread-safe and singletons.
class AlbumDataSource(private val context: Context) : DataSource.Albums {

    companion object {
        private val TAG = AlbumDataSource::class.simpleName
    }

    private val contentResolver: ContentResolver = context.contentResolver
    private val projection: Array<String> = arrayOf(
        _ID, ALBUM_ID, ALBUM, ARTIST_ID, NUMBER_OF_SONGS
    )

    override suspend fun getAlbums(limit: Int): List<Album> {
        val cursor = getCursor(sortOrder = "LIMIT $limit")

        return cursor!!.let {
            it.mapList(it, createAlbum(it))
        }
    }

    override suspend fun getAlbumsForArtist(artistId: Int): List<Album> {
        val cursor = getCursor("$ARTIST_ID = ?", arrayOf(artistId.toString()), "$ALBUM ASC")

        return cursor!!.let {
            it.mapList(it, createAlbum(it))
        }
    }


    //retrieving an album using a cursor object.
    // throws a null pointer exception if the cursor is null for some reason.
    override suspend fun getAlbum(id: Int): Album {
        val cursor = getCursor("$_ID = ?", arrayOf(id.toString()))

        return cursor?.use {
            if (cursor.moveToFirst()) {
                //creating album from cursor
                createAlbum(cursor)
            } else {
                //returning an empty album if cursor returns an empty list
                Log.i(TAG, "Empty result. No album matching ${id}? could be found")
                null
            }
        } ?: Album()
    }

    /**
     * @param selection is similar to sql's WHERE clause e.g WHERE name = "Tendai"
     * @param selectionArgs is similar to the "Tendai" string above except it should be an array when working with
     * content resolvers.
     */
    private fun getCursor(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): Cursor? =
        contentResolver.query(
            EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )


    //mapping results from the cursor to the album.
    private fun createAlbum(cursor: Cursor): Album =
        with(cursor) {
            Album(
                id = getInt(getColumnIndex(_ID)),
                albumId = getInt(getColumnIndex(ALBUM_ID)),
                albumTitle = getString(getColumnIndex(ALBUM)),
                artistName = getString(getColumnIndex(ARTIST)),
                numberOfTracks = getInt(getColumnIndex(NUMBER_OF_SONGS)),
                yearReleased = getInt(getColumnIndex(FIRST_YEAR)),
                artistId = getInt(getColumnIndex(ARTIST_ID))
            )
        }
}

//TODO: Is artist Id backward compatible