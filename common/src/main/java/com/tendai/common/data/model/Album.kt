package com.tendai.common.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Artist::class,
        parentColumns = ["artistId"],
        childColumns = ["artistId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Album(
    @PrimaryKey var albumId: Long,
    var albumTitle: String,
    var artistId: Int,
    var yearReleased: Date,
    var numberOfTracks: Int
)
