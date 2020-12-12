package com.tendai.common.data.model


data class Artist(
    var artistId: Int = -1,
    var artistName: String = "Empty",
    var numberOfAlbums: Int = 0,
    var numberOfTracks: Int = 0
)