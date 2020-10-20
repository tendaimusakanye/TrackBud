package com.tendai.common.data.model


data class Track(
    var id: Int = -1,
    var trackId: Int,
    var trackName: String,
    var albumId: Int,
    var albumName: String,
    var artistId: Int,
    var artistName: String,
    var duration: Int,
    var trackGenre: String,
    var trackNumber: Int,
    var playlistId: String

)
