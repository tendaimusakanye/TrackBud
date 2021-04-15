package com.tendai.common.source.model

import android.net.Uri


data class Track(
    var id: Long = -1L,
    var trackName: String = "",
    var albumId: Long = -1,
    var albumName: String = "",
    var artistId: Long = -1,
    var artistName: String = "",
    var duration: Int = 0,
    var trackGenre: String = "",
    var trackNumber: Int = -1,
    var playlistName: String = "",
    var albumArtUri: Uri = Uri.EMPTY
)
