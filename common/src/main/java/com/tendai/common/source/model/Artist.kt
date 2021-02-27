package com.tendai.common.source.model


data class Artist(
    var artistId: Int = -1,
    var artistName: String = "",
    var numberOfAlbums: Int = 0,
    var numberOfTracks: Int = 0
)