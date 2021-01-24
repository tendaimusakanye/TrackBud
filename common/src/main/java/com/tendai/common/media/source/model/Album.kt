package com.tendai.common.media.source.model


data class Album(
    var id: Int = -1,
    var albumTitle: String = "",
    var albumArtist: String = "",
    var artistId: Int = -1,
    var yearReleased: Int = -1,
    var numberOfTracks: Int = -1
)

//todo: remove artistId if its not being used in the  whole application.
