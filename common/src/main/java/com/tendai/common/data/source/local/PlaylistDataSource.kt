package com.tendai.common.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns._ID
import android.provider.MediaStore.Audio.Playlists.Members.*
import android.provider.MediaStore.Audio.PlaylistsColumns.NAME
import com.tendai.common.data.model.Playlist
import com.tendai.common.extensions.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI as PLAYLIST_URI

class PlaylistDataSource(private val context: Context) : DataSource.Playlists {

    private val contentResolver = context.contentResolver
    private val projection = arrayOf(
        NAME
    )
    private val playOrderProjection = arrayOf(
        _ID, NAME
    )

    override suspend fun getAllPlaylists(limit: Int): List<Playlist>? {
        return withContext(Dispatchers.IO) {
            val cursor =
                getCursor(
                    contentResolver,
                    PLAYLIST_URI,
                    projection,
                    sortOrder = "LIMIT $limit"
                )
            cursor?.let {
                it.mapList(it, mapToPlaylist(it))
            } ?: listOf()
        }
    }


    override suspend fun createNewPlaylist(name: String): Uri? {
        if (name.isEmpty()) return null
        return withContext(Dispatchers.IO) {
            //first query to check if there are other playlists with the same name.
            val cursor =
                getCursor(
                    contentResolver,
                    PLAYLIST_URI,
                    projection,
                    "$NAME = ?",
                    arrayOf(name)
                )
            cursor?.use {
                //checking to see if a playlist with @param name exists or not
                if (!cursor.moveToFirst()) {
                    //inserting values into the MediaStore.Audio.Playlist
                    val playlistDetails = ContentValues().apply {
                        put(NAME, name)
                    }
                    contentResolver.insert(PLAYLIST_URI, playlistDetails)
                } else {
                    Uri.EMPTY
                }
            }
        }


    }

    override suspend fun deletePlaylist(playlistId: Int): Int =
        withContext(Dispatchers.IO) {
            contentResolver.delete(
                PLAYLIST_URI,
                "$_ID = ?",
                arrayOf(playlistId.toString())
            )
        }


    override suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int {
        return withContext(Dispatchers.IO) {

            // this is done to get the correct Uri for us to insert the songs into the playlists.
            // That is just how android is
            val uri = getContentUri("external", playlistId.toLong())
            var playOrder = getHighestPlayOrder(playlistId)

            if (trackIds.isNotEmpty()) {
                val contentValues = Array(trackIds.size) { ContentValues() }
                if (playOrder != null) {
                    for (i in 0..trackIds.size) {
                        contentValues[i].put(AUDIO_ID, trackIds[i])
                        contentValues[i].put(PLAY_ORDER, playOrder++)
                    }
                }
                contentResolver.bulkInsert(uri, contentValues)
            } else {
                -1
            }
        }


    }

    override suspend fun removeTrackFromPlaylist(trackId: Long): Int =
        contentResolver.delete(PLAYLIST_URI, "$_ID = ?", arrayOf(trackId.toString()))

    override fun getNumberOfSongsInPlaylist(playlistId: Int): Int {
        if (playlistId == -1) return -1
        val uri = getContentUri("external", playlistId.toLong())
        val cursor =
            getCursor(
                contentResolver = contentResolver,
                uri = uri,
                projection = arrayOf(_ID)
            )
        return cursor?.count ?: -1
    }

    private fun mapToPlaylist(cursor: Cursor, playlistId: Int = -1): Playlist {
        return if (cursor.moveToFirst()) {
            with(cursor) {
                Playlist(
                    playlistId = getInt(getColumnIndex(PLAYLIST_ID)),
                    playlistName = getString(getColumnIndex(NAME)),
                    numberOfTracks = getNumberOfSongsInPlaylist(playlistId)
                )
            }
        } else {
            Playlist()
        }
    }


    //To add songs to the playlist, you also need to know the current high water mark of PLAY_ORDER
    // in the playlist's current state.
    // Otherwise the MediaStore ContentResolver will gag because you are trying
    // to insert playlist members with the same play order.
    // So, you need to query the Playlist Uri first to get the highest PLAY_ORDER value,
    // and use that as the starting point for your ContentValues inserts.
    private fun getHighestPlayOrder(playlistId: Int): Int? {

        val uri = getContentUri("external", playlistId.toLong())
        val cursor =
            getCursor(
                contentResolver,
                uri,
                playOrderProjection,
                sortOrder = "$PLAY_ORDER DESC"
            )

        return cursor?.use {
            if (it.moveToFirst()) {
                //getting the highest play_order and adding one i.e. using it as a starting point.
                val playOrder = it.getInt(it.getColumnIndex(PLAY_ORDER)) + 1
                playOrder
            } else {
                -1
            }
        }
    }
}

//TODO: Write tests against these methods to see if they work as expected.
//TODO: Methods insert and update should be thread safe? Look into that a bit?
