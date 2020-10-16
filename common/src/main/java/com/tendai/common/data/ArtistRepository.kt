package com.tendai.common.data

import com.tendai.common.data.model.Artist

class ArtistRepository: DataSource.Artists {
    override suspend fun getAllArtists(): List<Artist> {
        TODO("Not yet implemented")
    }

}