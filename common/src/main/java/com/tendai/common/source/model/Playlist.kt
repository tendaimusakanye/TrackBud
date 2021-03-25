package com.tendai.common.source.model


data class Playlist(
    var playlistId: Long = -1,
    var playlistName: String = "",
    var numberOfTracks: Int = -1
)