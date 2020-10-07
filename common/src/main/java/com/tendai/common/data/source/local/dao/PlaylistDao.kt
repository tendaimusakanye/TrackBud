package com.tendai.common.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tendai.common.data.model.Playlist

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlayList(playlist: Playlist)

    @Query("UPDATE playlist SET numberOfTracks = :numberOfTracks WHERE playlistId = :playlistId")
    suspend fun updateNumberOfTracksForPlaylist(numberOfTracks: Int, playlistId: Long)

    @Query("SELECT * FROM playlist ORDER BY playlistName ASC")
    suspend fun selectPlaylists(): List<Playlist>
}