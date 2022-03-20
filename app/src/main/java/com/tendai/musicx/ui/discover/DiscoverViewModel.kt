package com.tendai.musicx.ui.discover

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tendai.common.ClientServiceConnection
import com.tendai.common.DISCOVER_ROOT
import com.tendai.common.IS_ALBUM
import com.tendai.common.IS_PLAYLIST
import com.tendai.musicx.model.MediaItemData
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(private val connection: ClientServiceConnection) :
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
            _mediaItems.postValue(items)
        }
    }

    init {
        subscribe()
    }

    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems

    private fun subscribe() {
        val options = Bundle().apply {
            putBoolean(IS_ALBUM, false)
            putBoolean(IS_PLAYLIST, false)
        }
        connection.subscribe(DISCOVER_ROOT, options, subscriptionCallback)
    }

}