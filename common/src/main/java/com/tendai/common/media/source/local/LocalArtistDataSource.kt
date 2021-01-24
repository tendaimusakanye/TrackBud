package com.tendai.common.media.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.ArtistColumns.*
import android.provider.MediaStore.Audio.Artists._ID
import com.tendai.common.media.extensions.mapList
import com.tendai.common.media.source.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI as ARTISTS_URI

class ArtistLocalDataSource(context: Context) : LocalDataSource,
    LocalDataSource.Artists {

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
            cursor!!.use { result ->
                result.mapList { mapToArtist(it) }
            }
        }
    }

    private fun mapToArtist(cursor: Cursor): Artist =
        cursor.run {
            Artist(
                artistId = getInt(getColumnIndex(_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                numberOfAlbums = getInt(getColumnIndex(NUMBER_OF_ALBUMS)),
                numberOfTracks = getInt(getColumnIndex(NUMBER_OF_TRACKS))
            )
        }

}
private const val TAG = "LocalArtistDataSource"
//TODO: Check the size of the list/ check if list is empty always before
// retrieving any before calling the get function on variables.

// No Singletons only insert and update methods need to be thread safe of which I am using
// a synchronized lock. That is why I abandoned them.
