package com.tendai.musicx.ui.artist

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tendai.common.ARTISTS_ROOT
import com.tendai.common.ClientServiceConnection
import com.tendai.common.EXTRA_ARTIST_ID
import com.tendai.common.IS_ARTIST_ALBUMS
import com.tendai.musicx.model.MediaItemData
import javax.inject.Inject

class ArtistAlbumsViewModel @Inject constructor(private val connection: ClientServiceConnection) :
    ViewModel() {

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>,
            options: Bundle
        ) {
            val items = children.map { child ->
                MediaItemData(
                    id = child.mediaId!!,
                    name = child.description.title.toString(),
                    description = child.description.subtitle.toString(),
                    albumArtUri = child.description.iconUri,
                    browsable = child.isBrowsable
                )
            }
            _artistAlbums.postValue(items)
        }
    }

    private val _artistAlbums = MutableLiveData<List<MediaItemData>>()
    val artistAlbums: LiveData<List<MediaItemData>> = _artistAlbums

    fun subscribe(artistId: Long) {
        val options = Bundle().apply {
            putBoolean(IS_ARTIST_ALBUMS, true)
            putLong(EXTRA_ARTIST_ID, artistId)
        }
        connection.subscribe(ARTISTS_ROOT, options, subscriptionCallback)
    }
}