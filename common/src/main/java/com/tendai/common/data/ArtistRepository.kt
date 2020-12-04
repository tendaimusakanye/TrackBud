package com.tendai.common.data

import com.tendai.common.data.model.Artist
import com.tendai.common.data.source.local.ArtistDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArtistRepository(
    private val artistDataSource: ArtistDataSource
) : Repository.Artists {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var artists: List<Artist>? = null

    override fun getAllArtists(): List<Artist>? {
        return if (artists == null) {
            scope.launch {
                //try catch should be thrown here if this throws and exception but it doesn't
                // when the coroutine gets here it jumps out of this method and does other things. The code below is only
                //executed when the coroutine returns.
                artists = artistDataSource.getAllArtists()
            }
            artists
        } else {
            artists
        }
    }

}
