package com.tendai.common.source.model


data class Artist(
    var artistId: Long = -1,
    var artistName: String = "",
    var numberOfAlbums: Long = -1,
    var numberOfTracks: Long = -1
)
