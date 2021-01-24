package com.tendai.common.media.source

import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ArtistRepository(private val artistLocalDataSource: LocalDataSource.Artists) : Repository,
    Repository.Artists {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getAllArtists(): List<MediaMetadataCompat> =
        retrieveMediaItemList(scope = scope) { artistLocalDataSource.getAllArtists() }
}

