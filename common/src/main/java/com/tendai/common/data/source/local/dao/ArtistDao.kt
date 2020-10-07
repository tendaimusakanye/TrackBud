package com.tendai.common.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tendai.common.data.model.Artist

@Dao
interface ArtistDao {
//    @Insert
//    suspend fun insertArtist(artist: Artist)

    @Insert
    suspend fun insertArtists(vararg artists: Artist)

    @Delete
    suspend fun deleteArtists(vararg artist: Artist)

    @Query("UPDATE artist SET numberOfTracks = :numberOfTracks WHERE artistId = :artistId")
    suspend fun updateNumberOfTracksForArtist(numberOfTracks: Int, artistId: Int)

    @Query("UPDATE artist SET numberOfAlbums = :numberOfAlbums WHERE artistId = :artistId")
    suspend fun updateNumberOfAlbumsForArtist(numberOfAlbums: Int, artistId: Int)

    @Query("SELECT * FROM artist ORDER BY artistName ASC")
    suspend fun selectAllArtists(): List<Artist>

}