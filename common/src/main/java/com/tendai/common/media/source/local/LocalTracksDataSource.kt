package com.tendai.common.media.source.local

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.AudioColumns.TRACK
import android.provider.MediaStore.Audio.Media.*
import android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID
import android.provider.MediaStore.Audio.Playlists.Members.getContentUri
import android.provider.MediaStore.MediaColumns.DURATION
import com.tendai.common.media.extensions.mapList
import com.tendai.common.media.source.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI as TRACKS_URI

class TracksLocalDataSource(context: Context) : LocalDataSource,
    LocalDataSource.Tracks {

    private var fromPlaylist = false
    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID,
        TITLE,
        ALBUM_ID,
        ALBUM,
        ARTIST,
        ARTIST_ID,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DURATION
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
                fromPlaylist = false
                if (!cursor.moveToFirst()) return@use Track()
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
                fromPlaylist = false
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
                fromPlaylist = false
                result.mapList { mapToTrack(it) }
            }
        }

    }

    override suspend fun getTracksInAlbum(albumId: Int): List<Track> {
        return withContext(Dispatchers.IO) {
            val cursor = getCursor(
                contentResolver,
                TRACKS_URI,
                projection,
                "${IS_MUSIC}=1 AND $TITLE != ''AND $ALBUM_ID = ?",
                selectionArgs = arrayOf(albumId.toString())
            )
            cursor!!.use { result ->
                fromPlaylist = false
                result.mapList { mapToTrack(it) }
            }
        }
    }

    override suspend fun getTracksInPlaylist(playlistId: Int): List<Track> {
        return withContext(Dispatchers.IO) {
            //this is how you properly get tracks from a given playlistId
            val uri = getContentUri("external", playlistId.toLong())
            val cursor = getCursor(
                contentResolver,
                uri,
                projection
            )
            cursor!!.use { result ->
                fromPlaylist = true
                result.mapList { mapToTrack(it) }
            }
        }
    }

    private fun mapToTrack(cursor: Cursor): Track =
        cursor.run {
            Track(
                id = when (fromPlaylist) {
                    true -> getInt(getColumnIndex(AUDIO_ID))
                    else -> getInt(getColumnIndex(_ID))
                },
                trackName = getString(getColumnIndex(TITLE)),
                albumId = getInt(getColumnIndex(ALBUM_ID)),
                albumName = getString(getColumnIndex(ALBUM)),
                artistId = getInt(getColumnIndex(ARTIST_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                duration = getInt(getColumnIndex(DURATION)),
                trackNumber = getInt(getColumnIndex(TRACK)),
                albumArtUri = getAlbumArtUri(getInt(getColumnIndex(ALBUM_ID)))
            )
        }


}

private const val TAG = "LocalTracksDataSource"


