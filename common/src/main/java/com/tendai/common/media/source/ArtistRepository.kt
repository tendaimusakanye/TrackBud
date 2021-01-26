package com.tendai.common.media.source

import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.local.LocalDataSource

class ArtistRepository(private val artistLocalDataSource: LocalDataSource.Artists) : Repository,
    Repository.Artists {

    override fun getAllArtists(): List<MediaMetadataCompat> =
        retrieveMediaItemMetadataList() { artistLocalDataSource.getAllArtists() }
}

