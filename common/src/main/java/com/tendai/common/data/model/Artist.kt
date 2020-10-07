package com.tendai.common.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["artistId","artistName"],unique = true)])
data class Artist(
    @PrimaryKey var artistId: Long,
    var artistName: String,
    var numberOfAlbums: Int,
    var numberOfTracks: Int
)