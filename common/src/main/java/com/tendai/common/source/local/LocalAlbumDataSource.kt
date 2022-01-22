package com.tendai.common.source.local

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.provider.MediaStore.Audio.Albums.*
import android.provider.MediaStore.Audio.Artists.Albums.getContentUri
import android.provider.MediaStore.Audio.Media.ARTIST_ID
import com.tendai.common.extensions.mapToList
import com.tendai.common.source.model.Album
import javax.inject.Inject
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI as ALBUMS_URI

class LocalAlbumDataSource @Inject constructor(private val context: Context) : LocalDataSource,
    LocalDataSource.Albums {

    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, ALBUM, ARTIST, ARTIST_ID, FIRST_YEAR, NUMBER_OF_SONGS
    )

    //getAlbums for the discover page
    // Do not use the let scope function on the cursor as it does not close the cursor in case of
    // an error or something else.
    override fun getAlbums(limit: Int): List<Album> {
        val cursor = getCursor(
            contentResolver = contentResolver,
            uri = ALBUMS_URI,
            projection = projection,
            sortOrder = "LIMIT $limit"
        )
        return cursor!!.use { result ->
            result.mapToList { mapToAlbum(it) }
        }
    }

    // I am using the getCursor method for the flexibility of named arguments.
    // The content resolver's query method does not offer the flexibility named arguments.
    // if the cursor is null then something drastic happened. Let NPE be thrown otherwise we found
    // Nothing. therefore return an empty list or empty mediaItem.
    // are always returned.
    override fun getAlbumsByArtist(artistId: Long): List<Album> {
        val uri = getContentUri("external", artistId)
        val cursor = getCursor(
            contentResolver = contentResolver,
            uri = uri,
            projection = projection,
            sortOrder = "$ALBUM ASC"
        )
        return cursor!!.use { result ->
            result.mapToList { mapToAlbum(it) }
        }
    }

    override fun getAlbumDetails(albumId: Long): Album {
        val cursor = getCursor(
            contentResolver = contentResolver,
            uri = ALBUMS_URI,
            projection = projection,
            selection = "$_ID = ?",
            selectionArgs = arrayOf(albumId.toString())
        )
        return cursor!!.use {
            if (!cursor.moveToFirst()) return@use Album()
            mapToAlbum(it)
        }
    }

    /**
     * create an album from the cursor.
     * if moveToFirst is zero then no album or albums were found. return an empty list or an empty album
     * which makes sense as the cursor returns an empty list as well.
     */
    private fun mapToAlbum(cursor: Cursor): Album =
        cursor.run {
            Album(
                id = getLong(getColumnIndex(_ID)),
                albumTitle = getString(getColumnIndex(ALBUM)),
                albumArtist = getString(getColumnIndex(ARTIST)),
                artistId = getInt(getColumnIndex(ARTIST_ID)),
                yearReleased = getLong(getColumnIndex(FIRST_YEAR)),
                numberOfTracks = getLong(getColumnIndex(NUMBER_OF_SONGS)),
                albumArtUri = getAlbumArtUri(getLong(getColumnIndex(_ID)))
            )
        }
}

private const val TAG = "LocalAlbumDataSource"

