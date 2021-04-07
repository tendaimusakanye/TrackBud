package com.tendai.common.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.ArtistColumns.*
import android.provider.MediaStore.Audio.Artists._ID
import com.tendai.common.extensions.mapList
import com.tendai.common.source.model.Artist
import android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI as ARTISTS_URI

class ArtistLocalDataSource(context: Context) : LocalDataSource,
    LocalDataSource.Artists {

    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, ARTIST, NUMBER_OF_ALBUMS, NUMBER_OF_TRACKS
    )

    override fun getAllArtists(): List<Artist> {
        val cursor =
            createCursor(
                contentResolver,
                ARTISTS_URI,
                projection,
                sortOrder = "$ARTIST  ASC"
            )
        return cursor!!.use { result ->
            result.mapList { mapToArtist(it) }
        }
    }

    private fun mapToArtist(cursor: Cursor): Artist =
        cursor.run {
            Artist(
                artistId = getLong(getColumnIndex(_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                numberOfAlbums = getInt(getColumnIndex(NUMBER_OF_ALBUMS)),
                numberOfTracks = getInt(getColumnIndex(NUMBER_OF_TRACKS))
            )
        }

}
private const val TAG = "LocalArtistDataSource"


