package com.tendai.common.data.source.local.dao

import androidx.room.*
import com.tendai.common.data.model.Track

@Dao
interface TrackDao {

//    //todo: remove this if vararg can take one parameter
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertTrack(track: Track)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(vararg track: Track)

    @Delete
    suspend fun deleteTrack(vararg track: Track)

    @Query("SELECT * FROM track ORDER BY trackName ASC")
    suspend fun selectAllTracks(): List<Track>

    // All tracks for a particular artist
    @Query("SELECT * FROM track WHERE artistId = :artistId")
    suspend fun tracksForArtist(artistId: Long): List<Track>

    // All tracks in a particular playlist
    @Query("SELECT * FROM track WHERE playlistId = :playlistId")
    suspend fun tracksForPlaylist(playlistId: Long): List<Track>

    // All tracks in a particular album
    @Query("SELECT * FROM track WHERE albumId = :albumId")
    suspend fun tracksForAlbum(albumId: Long): List<Track>

    @Query("UPDATE track SET playlistId = :playlistId WHERE trackId = :trackId")
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)

}