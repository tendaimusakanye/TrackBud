package com.tendai.musicx.ui.artist

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tendai.common.ARTISTS_ROOT
import com.tendai.common.ClientServiceConnection
import com.tendai.common.IS_ALL_ARTISTS
import com.tendai.musicx.model.MediaItemData
import javax.inject.Inject

class ArtistViewModel @Inject constructor(private val connection: ClientServiceConnection) :
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
            _artists.postValue(items)
        }
    }

    init {
        subscribe()
    }

    private val _artists = MutableLiveData<List<MediaItemData>>()
    val artists: LiveData<List<MediaItemData>> = _artists

    private fun subscribe() {
        val options = Bundle().apply {
            putBoolean(IS_ALL_ARTISTS, true)
        }
        connection.subscribe(ARTISTS_ROOT, options, subscriptionCallback)
    }
}