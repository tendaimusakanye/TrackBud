package com.tendai.common.source.model

import android.net.Uri


data class Track(
    var id: Long = -1,
    var trackName: String = "",
    var albumId: Long = -1,
    var albumName: String = "",
    var artistId: Long = -1,
    var artistName: String = "",
    var duration: Int = 0,
    var trackGenre: String = "",
    var trackNumber: Int = -1,
    var playlistName: String = "",
    var albumArtUri: Uri? = null

)

//todo: also add albumArtBitmap but is it necessary or albumArtUri is enough ?
//todo: remove unused fields e.g. artistID, playlistID from both the models and the data sources.