package com.tendai.common.data.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.ArtistColumns.*
import android.provider.MediaStore.Audio.Artists._ID
import com.tendai.common.data.model.Artist
import com.tendai.common.extensions.mapList
import android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI as ARTISTS_URI

class ArtistDataSource(private val context: Context) : DataSource.Artists {

    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, ARTIST, NUMBER_OF_ALBUMS, NUMBER_OF_TRACKS
    )

    override fun getAllArtists(): List<Artist>? {
        //this is where execution is moved to a different thread
//     / /  return withContext(Dispatchers.IO) {
            val cursor =
                getCursor(
                    contentResolver,
                    ARTISTS_URI,
                    projection,
                    sortOrder = "$ARTIST  ASC"
                )

         return   cursor?.let {
                it.mapList(it, mapToArtist(it))
            } ?: listOf()
//       / }
    }

    private fun mapToArtist(cursor: Cursor): Artist {
        return if (cursor.moveToFirst()) {
            with(cursor) {
                Artist(
                    artistId = getInt(getColumnIndex(_ID)),
                    artistName = getString(getColumnIndex(ARTIST)),
                    numberOfAlbums = getInt(getColumnIndex(NUMBER_OF_ALBUMS)),
                    numberOfTracks = getInt(getColumnIndex(NUMBER_OF_TRACKS))
                )
            }
        } else {
            Artist()
        }
    }
}

//TODO: Check the size of the list before calling the get function.
