package com.tendai.common.source.local

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.AudioColumns.TRACK
import android.provider.MediaStore.Audio.Media.*
import android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID
import android.provider.MediaStore.Audio.Playlists.Members.getContentUri
import android.provider.MediaStore.MediaColumns.DURATION
import com.tendai.common.R
import com.tendai.common.extensions.mapToList
import com.tendai.common.source.model.Track
import java.io.IOException
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI as TRACKS_URI

class TracksLocalDataSource(private val context: Context) : LocalDataSource,
    LocalDataSource.Tracks {

    private var fromPlaylist = false
    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        _ID, TITLE, ALBUM_ID, ALBUM, ARTIST, ARTIST_ID,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DURATION
    )

    override fun getTrackDetails(trackId: Long): Track {
        val cursor = createCursor(
            contentResolver,
            TRACKS_URI,
            projection,
            "$_ID = ? AND ${IS_MUSIC}= 1 AND $TITLE != ''",
            selectionArgs = arrayOf(trackId.toString())
        )
        return cursor!!.use {
            fromPlaylist = false
            if (!cursor.moveToFirst()) return@use Track()
            mapToTrack(it)
        }
    }

    override fun getTracks(): List<Track> {
        val cursor = createCursor(
            contentResolver,
            TRACKS_URI,
            projection,
            "${IS_MUSIC}=1 AND $TITLE != ''"
        )
        return cursor!!.use { result ->
            fromPlaylist = false
            result.mapToList { mapToTrack(it) }
        }
    }

    override fun getTracksByArtist(artistId: Long): List<Track> {
        val cursor = createCursor(
            contentResolver,
            TRACKS_URI,
            projection,
            "${IS_MUSIC}=1 AND $TITLE != ''AND $ARTIST_ID = ?",
            selectionArgs = arrayOf(artistId.toString())
        )
        return cursor!!.use { result ->
            fromPlaylist = false
            result.mapToList { mapToTrack(it) }
        }
    }

    override fun getTracksInAlbum(albumId: Long): List<Track> {
        val cursor = createCursor(
            contentResolver,
            TRACKS_URI,
            projection,
            "${IS_MUSIC}=1 AND $TITLE != ''AND $ALBUM_ID = ?",
            selectionArgs = arrayOf(albumId.toString())
        )
        return cursor!!.use { result ->
            fromPlaylist = false
            result.mapToList { mapToTrack(it) }
        }
    }

    override fun getTracksInPlaylist(playlistId: Long): List<Track> {
        //this is how you properly get tracks from a given playlistId
        val uri = getContentUri("external", playlistId)
        val cursor = createCursor(
            contentResolver,
            uri,
            projection
        )
        return cursor!!.use { result ->
            fromPlaylist = true
            result.mapToList { mapToTrack(it) }
        }
    }

    @Suppress("DEPRECATION")
    override fun getAlbumArt(albumId: Long): Bitmap {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, getAlbumArtUri(albumId))
                return ImageDecoder.decodeBitmap(source)
            }
            return MediaStore.Images.Media.getBitmap(contentResolver, getAlbumArtUri(albumId))
        } catch (e: IOException) {
            return BitmapFactory.decodeResource(context.resources, R.drawable.ic_placeholder_art)
        }
    }

    private fun mapToTrack(cursor: Cursor): Track =
        cursor.run {
            Track(
                id = when (fromPlaylist) {
                    true -> getLong(getColumnIndex(AUDIO_ID))
                    else -> getLong(getColumnIndex(_ID))
                },
                playlistName = when (fromPlaylist) {
                    true -> getString(getColumnIndex(MediaStore.Audio.Playlists.NAME))
                    else -> ""
                },
                trackName = getString(getColumnIndex(TITLE)),
                albumId = getLong(getColumnIndex(ALBUM_ID)),
                albumName = getString(getColumnIndex(ALBUM)),
                artistId = getLong(getColumnIndex(ARTIST_ID)),
                artistName = getString(getColumnIndex(ARTIST)),
                duration = getInt(getColumnIndex(DURATION)),
                trackNumber = getInt(getColumnIndex(TRACK)),
                albumArtUri = getAlbumArtUri(getLong(getColumnIndex(ALBUM_ID)))
            )
        }
}

private const val TAG = "LocalTracksDataSource"


