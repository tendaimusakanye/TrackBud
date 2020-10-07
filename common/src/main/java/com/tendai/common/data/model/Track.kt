package com.tendai.common.data.model

import android.graphics.Bitmap
import androidx.room.*

@Entity(
    indices = [Index(value = ["trackName", "albumName", "artistName"])],
    foreignKeys = [ForeignKey(
        entity = Album::class,
        parentColumns = ["albumId"],
        childColumns = ["albumId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Artist::class,
        parentColumns = ["artistId"],
        childColumns = ["artistId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Playlist::class,
        parentColumns = ["playlistId"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Track(
    @PrimaryKey var trackId: Long,
    var trackName: String,
    var albumId: Long,
    var albumName: String,
    var artistId: Long,
    var artistName: String,
    var trackLength: Long,
    var trackGenre: String,
    var trackNumber: Int,
    var playlistId: String,
    var trackPath: String,
    @Ignore var albumArt: Bitmap
)
