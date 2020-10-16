package com.tendai.common.data.source.local

import com.tendai.common.data.DataSource
import com.tendai.common.data.model.Artist

class ArtistDataSource : DataSource.Artists {
    override suspend fun getAllArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

}