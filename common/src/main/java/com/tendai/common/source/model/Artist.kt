package com.tendai.common.source.model


data class Artist(
    var artistId: Long = -1,
    var artistName: String = "",
    var numberOfAlbums: Int = 0,
    var numberOfTracks: Int = 0
)

//todo: refactor and minimize the casts