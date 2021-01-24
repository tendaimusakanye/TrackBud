package com.tendai.common.media.source.model


data class Track(
    var id: Int = -1,
    var trackName: String = "",
    var albumId: Int = -1,
    var albumName: String = "",
    var artistId: Int = -1,
    var artistName: String = "",
    var duration: Int = 0,
    var trackGenre: String = "",
    var trackNumber: Int = -1,
    var playlistId: String = ""

)
//todo: remove unused fields e.g. artistID, playlistID from both the models and the data sources.