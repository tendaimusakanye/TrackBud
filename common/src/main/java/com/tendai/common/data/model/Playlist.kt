package com.tendai.common.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey var playlistId: Long,
    var playlistName: String,
    var numberOfTracks: Int
)