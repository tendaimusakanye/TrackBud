package com.tendai.common.data.source.local

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.*
import android.provider.MediaStore.Audio.GenresColumns.NAME
import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Track

class TracksDataSource(private val context: Context) : DataSource.Tracks {


    private val contentResolver: ContentResolver = context.contentResolver
    private val projection: Array<String> = arrayOf(

    )

    override suspend fun getTracks(): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksForArtist(artistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksForAlbums(albumId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    private fun createTrack(cursor: Cursor): Track =
        with(cursor) {
            Track(
                duration = getInt(getColumnIndex(DURATION)),
                id = getInt(getColumnIndex(_ID)),
                trackId = getInt(getColumnIndex(_)),,
                trackName = getString(getColumnIndex(TITLE)) ,
                albumId = getInt(getColumnIndex(ALBUM_ID)),
                albumName = getString(getColumnIndex(ALBUM)),
                artistId = getInt(getColumnIndex(ARTIST_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                trackGenre = getString(getColumnIndex(NAME)),
                trackNumber =getInt(getColumnIndex(TRACK)),
                playlistId = ""

                )
        }


    private fun getCursor(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): Cursor? =
        contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

}

