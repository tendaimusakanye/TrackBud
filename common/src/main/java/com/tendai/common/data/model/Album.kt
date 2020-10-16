package com.tendai.common.data.model


import java.util.*

data class Album(
    var albumId: Long,
    var albumTitle: String,
    var artistId: Int,
    var yearReleased: Date,
    var numberOfTracks: Int
)
