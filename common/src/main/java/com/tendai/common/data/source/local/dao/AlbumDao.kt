package com.tendai.common.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tendai.common.data.model.Album

@Dao
interface AlbumDao {
//    @Insert
//    suspend fun insertAlbum(album: Album)

    @Insert
    suspend fun insertAlbums(vararg albums: Album)

    @Delete
    suspend fun deleteAlbums(vararg albums: Album)

    @Query("SELECT albumTitle FROM album ORDER BY albumTitle ASC LIMIT 5")
    suspend fun selectAlbums(): List<Album>

    // All albums for a particular artist
    @Query("SELECT * FROM album WHERE artistId = :artistId")
    suspend fun albumsForArtist(artistId: Long): List<Album>
}