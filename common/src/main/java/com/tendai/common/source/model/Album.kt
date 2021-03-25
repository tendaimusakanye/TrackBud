package com.tendai.common.source.model

import android.graphics.Bitmap
import android.net.Uri


data class Album(
    var id: Long = -1,
    var albumTitle: String = "",
    var albumArtist: String = "",
    var artistId: Int = -1,
    var yearReleased: Int = -1,
    var numberOfTracks: Int = -1,
    var albumArtUri: Uri? = null,
    var albumArt: Bitmap? = null
)


//TODO(Is the Bitmap necessary or android can still load it from the Uri ??)

//TODO(: look for the non-deprecated way to fetch the albumArt bitmap
// from MediaStore and add it to the MetaDataCompatBuilder.ext album metaData)

//todo: remove artistId if its not being used in the  whole application.
