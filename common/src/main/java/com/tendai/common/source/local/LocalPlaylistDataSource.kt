package com.tendai.common.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns._ID
import android.provider.MediaStore.Audio.Playlists.Members.*
import android.provider.MediaStore.Audio.PlaylistsColumns.NAME
import android.util.Log
import com.tendai.common.extensions.mapToList
import com.tendai.common.source.model.Playlist
import android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI as PLAYLIST_URI

class LocalPlaylistDataSource(context: Context) : LocalDataSource,
    LocalDataSource.Playlists {

    private val contentResolver = context.contentResolver
    //playlist_Id should automatically be inserted I think. Yet to see.
    private val newPlaylistProjection = arrayOf(
        PLAYLIST_ID, NAME
    )

    // is the _ID necessary in the playOrder projection ?
    //todo: remove it if it is not necessary.
    private val playOrderProjection = arrayOf(
        _ID, NAME, PLAY_ORDER
    )

    override  suspend  fun getAllPlaylists(limit: Int): List<Playlist> {
        val cursor =
            createCursor(
                contentResolver,
                PLAYLIST_URI,
                newPlaylistProjection,
                sortOrder = "LIMIT $limit"
            )
        return cursor!!.use { result ->
            result.mapToList { mapToPlaylist(it) }
        }
    }

    // android is forcing me to return a null Uri here
    override  suspend  fun createNewPlaylist(name: String): Uri? {
        if (name.isEmpty()) return null
        //first query to check if there are other playlists with the same name.
        return synchronized(this) {
            val cursor =
                createCursor(
                    contentResolver,
                    PLAYLIST_URI,
                    newPlaylistProjection,
                    "$NAME = ?",
                    arrayOf(name)
                )

            cursor!!.use {
                //checking to see if a playlist with @param name exists or not
                if (!cursor.moveToFirst()) {
                    //inserting values into the MediaStore.Audio.Playlist
                    val playlistDetails = ContentValues().apply {
                        put(NAME, name)
                    }
                    contentResolver.insert(PLAYLIST_URI, playlistDetails)
                } else {
                    Log.i(TAG, "Playlist with the given name already exists")
                    Uri.EMPTY
                }
            }
        }
    }

    override  suspend  fun deletePlaylist(playlistId: Int): Int =
        contentResolver.delete(
            PLAYLIST_URI,
            "$PLAYLIST_ID = ?",
            arrayOf(playlistId.toString())
        )


    override  suspend  fun addTracksToPlaylist(playlistId: Long, trackIds: LongArray): Int {
        // this is done to get the correct Uri for us to insert the songs into the playlists.
        // That is just how android is
        // android recommends insert and update methods be thread safe. i.e. the reason for the synchronized block
        return synchronized(this) {
            val uri = getContentUri("external", playlistId)
            var playOrder = getHighestPlayOrder(playlistId.toInt())

            if (trackIds.isNotEmpty()) {
                val contentValues = Array(trackIds.size) { ContentValues() }
                if (playOrder != -1) {
                    for (i in trackIds.indices) {
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

    override suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int =
        contentResolver.delete(PLAYLIST_URI, "$AUDIO_ID = ?", arrayOf(trackIds.toString()))

    private fun getNumberOfSongsInPlaylist(playlistId: Long): Int {
        if (playlistId == -1L) return -1
        val uri = getContentUri("external", playlistId)
        val cursor =
            createCursor(
                contentResolver = contentResolver,
                uri = uri,
                projection = arrayOf(AUDIO_ID)
            )
        return cursor!!.count
    }

    //To add songs to the playlist, you also need to know the current high water mark of PLAY_ORDER
    // in the playlist's current state.
    // Otherwise the MediaStore ContentResolver will gag because you are trying
    // to insert playlist members with the same play order.
    // So, you need to query the Playlist Uri first to get the highest PLAY_ORDER value,
    // and use that as the starting point for your ContentValues inserts.
    private fun getHighestPlayOrder(playlistId: Int): Int {
        val uri = getContentUri("external", playlistId.toLong())
        val cursor =
            createCursor(
                contentResolver,
                uri,
                playOrderProjection,
                sortOrder = "$PLAY_ORDER DESC"
            )
        return cursor!!.use {
            if (it.moveToFirst()) {
                //getting the highest play_order and adding one i.e. using it as a starting point.
                val playOrder = it.getInt(it.getColumnIndex(PLAY_ORDER)) + 1
                playOrder
            } else {
                -1
            }
        }
    }

    private fun mapToPlaylist(cursor: Cursor): Playlist =
        cursor.run {
            Playlist(
                playlistId = getLong(getColumnIndex(PLAYLIST_ID)),
                playlistName = getString(getColumnIndex(NAME)),
                numberOfTracks = getNumberOfSongsInPlaylist(getLong(getColumnIndex(PLAYLIST_ID))).toLong()
            )
        }
}

private const val TAG = "LocalPlaylistDataSource"
//todo: check the ints returned when dealing with playlists. if -1 then respond appropriately

