package com.tendai.common.media.source

import com.tendai.common.media.source.model.Artist
import com.tendai.common.media.source.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ArtistRepository(private val artistLocalDataSource: LocalDataSource.Artists) : Repository,
    Repository.Artists {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getAllArtists(): List<Artist> =
        retrieveMediaItemList(scope = scope) { artistLocalDataSource.getAllArtists() }
}

