package com.tendai.common.data.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.ArtistColumns.*
import android.provider.MediaStore.Audio.Artists._ID
import android.util.Log
import com.tendai.common.data.DataSource
import com.tendai.common.data.getCursor
import com.tendai.common.data.model.Artist
import com.tendai.common.extensions.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI as ARTISTS_URI

class ArtistDataSource(private val context: Context) : DataSource.Artists {

    private val ioDispatcher = Dispatchers.IO
    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, ARTIST, NUMBER_OF_ALBUMS, NUMBER_OF_TRACKS
    )

    override suspend fun getAllArtists(): List<Artist> {
        //this is where execution is moved to a different thread
        return withContext(ioDispatcher) {
            val cursor =
                getCursor(
                    contentResolver,
                    ARTISTS_URI,
                    projection,
                    sortOrder = "$ARTIST  ASC"
                )
            cursor!!.use {
                it.mapList(mapToArtist(it))
            }
        }
    }

    private fun mapToArtist(cursor: Cursor): Artist {
        return if (cursor.moveToFirst()) {
            cursor.run{
                Artist(
                    artistId = getInt(getColumnIndex(_ID)),
                    artistName = getString(getColumnIndex(ARTIST)),
                    numberOfAlbums = getInt(getColumnIndex(NUMBER_OF_ALBUMS)),
                    numberOfTracks = getInt(getColumnIndex(NUMBER_OF_TRACKS))
                )
            }
        } else {
            Log.e(TAG, "Cursor was empty")
            Artist()
        }
    }
}
private const val TAG = "ArtistDataSource"
//TODO: Check the size of the list/ check if list is empty always before re
// retrieving any before calling the get function on variables.
//TODO: Singletons or not ?? why did I abandon them by the way ?
