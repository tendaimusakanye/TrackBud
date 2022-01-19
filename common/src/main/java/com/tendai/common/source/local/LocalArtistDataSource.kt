package com.tendai.common.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.ArtistColumns.*
import android.provider.MediaStore.Audio.Artists._ID
import com.tendai.common.extensions.mapToList
import com.tendai.common.source.model.Artist
import android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI as ARTISTS_URI

class LocalArtistDataSource(context: Context) : LocalDataSource,
    LocalDataSource.Artists {

    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, ARTIST, NUMBER_OF_ALBUMS, NUMBER_OF_TRACKS
    )

    override fun getAllArtists(): List<Artist> {
        val cursor =
            getCursor(
                contentResolver,
                ARTISTS_URI,
                projection,
                sortOrder = "$ARTIST ASC"
            )
        return cursor!!.use { result ->
            result.mapToList { mapToArtist(it) }
        }
    }

    private fun mapToArtist(cursor: Cursor): Artist =
        cursor.run {
            Artist(
                artistId = getLong(getColumnIndex(_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                numberOfAlbums = getLong(getColumnIndex(NUMBER_OF_ALBUMS)),
                numberOfTracks = getLong(getColumnIndex(NUMBER_OF_TRACKS))
            )
        }

}

private const val TAG = "LocalArtistDataSource"


