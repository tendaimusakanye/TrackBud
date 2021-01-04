package com.tendai.common.data.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.Media.*
import android.provider.MediaStore.Audio.Playlists.Members.DURATION
import android.provider.MediaStore.Audio.Playlists.Members.getContentUri
import com.tendai.common.data.DataSource
import com.tendai.common.data.getCursor
import com.tendai.common.data.model.Track
import com.tendai.common.extensions.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI as TRACKS_URI

class TracksDataSource(private val context: Context) : DataSource.Tracks {

    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, TITLE, ALBUM_ID, ALBUM, ARTIST, ARTIST_ID, TRACK, DURATION
    )

    override suspend fun getTrackDetails(trackId: Int): Track {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "$_ID = ? AND ${IS_MUSIC}= 1 AND $TITLE != ''",
                selectionArgs = arrayOf(trackId.toString())
            )
            cursor!!.use {
                mapToTrack(it)
            }
        }

    }

    override suspend fun getTracks(): List<Track> {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "${IS_MUSIC}=1 AND $TITLE != ''"
            )
            cursor!!.use { result ->
                result.mapList { mapToTrack(it) }
            }
        }
    }

    override suspend fun getTracksForArtist(artistId: Int): List<Track> {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "${IS_MUSIC}=1 AND $TITLE != ''AND $ARTIST_ID = ?",
                selectionArgs = arrayOf(artistId.toString())
            )
            cursor!!.use { result ->
                result.mapList { mapToTrack(it) }
            }
        }

    }

    override suspend fun getTracksForAlbum(albumId: Int): List<Track> {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "${IS_MUSIC}=1 AND $TITLE != ''AND $ALBUM_ID = ?",
                selectionArgs = arrayOf(albumId.toString())
            )
            cursor!!.use { result ->
                result.mapList { mapToTrack(it) }
            }
        }
    }

    override suspend fun getTracksForPlaylist(playlistId: Int): List<Track> {
        return withContext(Dispatchers.IO) {
            //this is how you properly get tracks from a given playlistId
            val uri = getContentUri("external", playlistId.toLong())
            val cursor = getCursor(
                contentResolver,
                uri,
                projection
            )
            cursor!!.use { result ->
                result.mapList { mapToTrack(it) }
            }
        }
    }

    private fun mapToTrack(cursor: Cursor): Track =
        cursor.run {
            Track(
                duration = getInt(getColumnIndex(DURATION)),
                id = getInt(getColumnIndex(_ID)),
                trackName = getString(getColumnIndex(TITLE)),
                albumId = getInt(getColumnIndex(ALBUM_ID)),
                albumName = getString(getColumnIndex(ALBUM)),
                artistId = getInt(getColumnIndex(ARTIST_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                trackNumber = getInt(getColumnIndex(TRACK))
            )
        }

}
private const val TAG = "TracksDataSource"


