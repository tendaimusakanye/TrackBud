package com.tendai.common.data.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.AudioColumns.*
import android.provider.MediaStore.Audio.Playlists.Members.getContentUri
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

    override suspend fun getTracks(): List<Track>? {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "${IS_MUSIC}=1 AND $TITLE != ''"
            )

            cursor?.let {
                it.mapList(it, mapToTrack(it))
            } ?: listOf()
        }
    }

    override suspend fun getTracksForArtist(artistId: Long): List<Track>? {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "${IS_MUSIC}=1 AND $TITLE != ''AND $ARTIST_ID = ?",
                arrayOf(artistId.toString())
            )
            cursor?.let {
                it.mapList(it, mapToTrack(it))
            } ?: listOf()
        }

    }

    override suspend fun getTracksForAlbum(albumId: Long): List<Track>? {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "${IS_MUSIC}=1 AND $TITLE != ''AND $ALBUM_ID = ?",
                arrayOf(albumId.toString())
            )

            cursor?.let {
                it.mapList(it, mapToTrack(it))
            } ?: listOf()
        }
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track>? {
        return withContext(Dispatchers.IO) {
            //this is how you properly get tracks from a given playlistId
            val uri = getContentUri("external", playlistId)
            val cursor = getCursor(
                contentResolver,
                uri,
                projection
            )

            cursor?.let {
                it.mapList(it, mapToTrack(it))
            } ?: listOf()
        }

    }

    private fun mapToTrack(cursor: Cursor): Track {
        return if (cursor.moveToFirst()) {
            with(cursor) {
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
        } else {
            Track()
        }
    }
}

