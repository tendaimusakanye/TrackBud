package com.tendai.common.data.source.local

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Audio.ArtistColumns.*
import android.provider.MediaStore.Audio.Artists._ID
import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Artist
import com.tendai.common.extensions.mapList

class ArtistDataSource(private val context: Context) : DataSource.Artists {

    private val contentResolver: ContentResolver = context.contentResolver
    private val projection: Array<String> = arrayOf(
        _ID, ARTIST, NUMBER_OF_ALBUMS, NUMBER_OF_TRACKS
    )

    override suspend fun getAllArtists(): List<Artist> {
        val cursor = getCursor(sortOrder = "$ARTIST  ASC")

        return cursor!!.let {
            it.mapList(it, createArtist(it))
        }
    }

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

    private fun createArtist(cursor: Cursor): Artist =
        with(cursor) {
            Artist(
                artistId = getInt(getColumnIndex(_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                numberOfAlbums = getInt(getColumnIndex(NUMBER_OF_ALBUMS)),
                numberOfTracks = getInt(getColumnIndex(NUMBER_OF_TRACKS))
            )
        }

}