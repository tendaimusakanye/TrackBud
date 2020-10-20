package com.tendai.common.data.model


data class Album(
    var id: Int = -1,
    var albumId: Int = -1,
    var albumTitle: String = "",
    var artistName: String = "",
    var artistId:Int = -1,
    var yearReleased: Int = -1,
    var numberOfTracks: Int = -1
)
