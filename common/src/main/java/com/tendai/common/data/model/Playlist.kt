package com.tendai.common.data.model


data class Playlist(
    var id: Long = -1,
    var playlistId: Long,
    var playlistName: String,
    var numberOfTracks: Int
)