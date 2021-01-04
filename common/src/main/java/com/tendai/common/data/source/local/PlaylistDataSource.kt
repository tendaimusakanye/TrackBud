package com.tendai.common.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns._ID
import android.provider.MediaStore.Audio.Playlists.Members.*
import android.provider.MediaStore.Audio.PlaylistsColumns.NAME
import android.util.Log
import com.tendai.common.data.DataSource
import com.tendai.common.data.getCursor
import com.tendai.common.data.model.Playlist
import com.tendai.common.extensions.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI as PLAYLIST_URI

class PlaylistDataSource(private val context: Context) : DataSource.Playlists {

    private val ioDispatcher = Dispatchers.IO
    private val contentResolver = context.contentResolver

    //playlist_Id should automatically inserted I think. Yet to see.
    private val newPlaylistProjection = arrayOf(
        PLAYLIST_ID, NAME
    )

    // the playOrder column should be already there within the MediaStore. Android or should ?
    private val playOrderProjection = arrayOf(
        _ID, NAME, PLAY_ORDER
    )

    override suspend fun getAllPlaylists(limit: Int): List<Playlist> {
        return withContext(ioDispatcher) {
            val cursor =
                getCursor(
                    contentResolver,
                    PLAYLIST_URI,
                    newPlaylistProjection,
                    sortOrder = "LIMIT $limit"
                )
            cursor!!.use { result ->
                result.mapList { mapToPlaylist(it) }
            }
        }
    }

    // android is forcing me to return a null Uri here
    override suspend fun createNewPlaylist(name: String): Uri? {
        if (name.isEmpty()) return null
        return withContext(ioDispatcher) {
            //first query to check if there are other playlists with the same name.
            synchronized(this) {
                val cursor =
                    getCursor(
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
    }

    override suspend fun deletePlaylist(playlistId: Int): Int =
        withContext(ioDispatcher) {
            contentResolver.delete(
                PLAYLIST_URI,
                "$_ID = ?",
                arrayOf(playlistId.toString())
            )
        }

    override suspend fun addTracksToPlaylist(playlistId: Int, trackIds: LongArray): Int {
        return withContext(ioDispatcher) {
            // this is done to get the correct Uri for us to insert the songs into the playlists.
            // That is just how android is
            // android recommends insert and update methods be thread safe. i.e. the reason for the synchronized block
            synchronized(this) {
                val uri = getContentUri("external", playlistId.toLong())
                var playOrder = getHighestPlayOrder(playlistId)

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
    }

    override suspend fun removeTrackFromPlaylist(trackIds: LongArray): Int =
        withContext(ioDispatcher) {
            contentResolver.delete(PLAYLIST_URI, "$AUDIO_ID = ?", arrayOf(trackIds.toString()))
        }


    // this is called inside withContext already so no need to make any further computations.
    private fun getNumberOfSongsInPlaylist(playlistId: Int): Int {
        if (playlistId == -1) return -1
        val uri = getContentUri("external", playlistId.toLong())
        val cursor =
            getCursor(
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
            getCursor(
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
                playlistId = getInt(getColumnIndex(PLAYLIST_ID)),
                playlistName = getString(getColumnIndex(NAME)),
                numberOfTracks = getNumberOfSongsInPlaylist(getInt(getColumnIndex(PLAYLIST_ID)))
            )
        }

}

private const val TAG = "PlaylistDataSource"


//todo: check the ints returned when dealing with playlists. if -1 then respond appropriately

