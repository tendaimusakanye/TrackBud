package com.tendai.common.data

import com.tendai.common.data.model.Artist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ArtistRepository(
    private val artistDataSource: DataSource.Artists
) : DataSource.Artists {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override suspend fun getAllArtists(): List<Artist> =
        retrieveMediaItemList(scope = scope) { artistDataSource.getAllArtists() }
}

