package com.tendai.common.data.model

import android.graphics.Bitmap


data class Track(
   var trackId: Long,
    var trackName: String,
    var albumId: Long,
    var albumName: String,
    var artistId: Long,
    var artistName: String,
    var trackLength: Long,
    var trackGenre: String,
    var trackNumber: Int,
    var playlistId: String,
    var trackPath: String,
    var albumArt: Bitmap

)
