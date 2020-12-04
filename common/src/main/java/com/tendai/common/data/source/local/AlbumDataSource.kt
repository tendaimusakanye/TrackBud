package com.tendai.common.data.source.local

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.provider.MediaStore.Audio.AlbumColumns.*
import android.provider.MediaStore.Audio.Artists.Albums.getContentUri
import android.util.Log
import com.tendai.common.data.model.Album
import com.tendai.common.extensions.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI as ALBUMS_URI

class AlbumDataSource(private val context: Context) : DataSource.Albums {

    companion object {
        private val TAG = AlbumDataSource::class.simpleName

        @Volatile
        private var INSTANCE: AlbumDataSource? = null

        fun getInstance(context: Context): AlbumDataSource? {
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AlbumDataSource(context)
            }
            return INSTANCE
        }
    }

    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, ALBUM_ID, ALBUM, ARTIST, ARTIST_ID, FIRST_YEAR, NUMBER_OF_SONGS
    )

    //getAlbums for the discover page
    override suspend fun getAlbums(limit: Int): List<Album>? {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver = contentResolver,
                uri = ALBUMS_URI,
                projection = projection,
                sortOrder = "LIMIT $limit"
            )

            cursor?.let {
                it.mapList(it, mapToAlbum(it))
            } ?: listOf()

        }
    }

    override suspend fun getAlbumsForArtist(artistId: Int): List<Album>? {
        return withContext(Dispatchers.IO) {
            val uri = getContentUri("external", artistId.toLong())
            val cursor = getCursor(
                contentResolver = contentResolver,
                uri = uri,
                projection = projection,
                sortOrder = "$ALBUM ASC"
            )

            cursor?.let {
                it.mapList(it, mapToAlbum(it))
            } ?: listOf()
        }
    }

    override suspend fun getAlbum(albumId: Int): Album? {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver = contentResolver,
                uri = ALBUMS_URI,
                projection = projection,
                selection = "$_ID = ?",
                selectionArgs = arrayOf(albumId.toString())
            )
            cursor?.use {
                if (cursor.count != 0) {
                    //creating album from cursor
                    mapToAlbum(cursor)
                } else {
                    //returning an empty album if cursor returns an empty list
                    Log.i(TAG, "Empty result. No album matching ${albumId}? could be found")
                    null
                }
            } ?: Album()
        }
    }

    private fun mapToAlbum(cursor: Cursor): Album {
        return if (cursor.moveToFirst()) {
            with(cursor) {
                Album(
                    id = getLong(getColumnIndex(_ID)),
                    albumId = getInt(getColumnIndex(ALBUM_ID)),
                    albumTitle = getString(getColumnIndex(ALBUM)),
                    artistName = getString(getColumnIndex(ARTIST)),
                    numberOfTracks = getInt(getColumnIndex(NUMBER_OF_SONGS)),
                    yearReleased = getInt(getColumnIndex(FIRST_YEAR)),
                    artistId = getInt(getColumnIndex(ARTIST_ID))
                )
            }
        } else {
            Album()
        }
    }
}

//TODO: Make these data source classes thread-safe and singletons.
//TODO: Handle ARTIST_ID for lower APIs
//TODO: Implement Error handling not just return empty lists and objects.