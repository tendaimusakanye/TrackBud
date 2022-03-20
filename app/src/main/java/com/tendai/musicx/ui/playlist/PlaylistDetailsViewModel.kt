package com.tendai.musicx.ui.playlist

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tendai.common.ClientServiceConnection
import com.tendai.common.DISCOVER_ROOT
import com.tendai.common.EXTRA_PLAYLIST_ID
import com.tendai.common.IS_PLAYLIST
import com.tendai.musicx.model.MediaItemData
import javax.inject.Inject

class PlaylistDetailsViewModel @Inject constructor(private val connection: ClientServiceConnection) :
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
            _tracks.postValue(items)
        }
    }

    private val _tracks = MutableLiveData<List<MediaItemData>>()
    val tracks: LiveData<List<MediaItemData>> = _tracks

    fun subscribe(playlistId: Long) {
        val options = Bundle().apply {
            putBoolean(IS_PLAYLIST, true)
            putLong(EXTRA_PLAYLIST_ID, playlistId)
        }
        connection.subscribe(DISCOVER_ROOT, options, subscriptionCallback)
    }
}