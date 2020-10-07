package com.tendai.common.data.source.local

import androidx.room.Database
import com.tendai.common.data.model.Album
import com.tendai.common.data.model.Artist
import com.tendai.common.data.model.Playlist
import com.tendai.common.data.model.Track
import com.tendai.common.data.source.local.dao.AlbumDao
import com.tendai.common.data.source.local.dao.ArtistDao
import com.tendai.common.data.source.local.dao.PlaylistDao
import com.tendai.common.data.source.local.dao.TrackDao

@Database(
    entities = [Track::class, Playlist::class, Artist::class, Album::class],
    version = 1,
    exportSchema = true
)
abstract class MusicDatabase {
    abstract fun albumDao(): AlbumDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
    abstract fun artistDao(): ArtistDao
}