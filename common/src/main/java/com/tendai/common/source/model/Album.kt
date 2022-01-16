package com.tendai.common.source.model

import android.net.Uri


data class Album(
    var id: Long = -1L,
    var albumTitle: String = "",
    var albumArtist: String = "",
    var artistId: Int = -1,
    var yearReleased: Long = -1,
    var numberOfTracks: Long = -1,
    var albumArtUri: Uri? = null
)


