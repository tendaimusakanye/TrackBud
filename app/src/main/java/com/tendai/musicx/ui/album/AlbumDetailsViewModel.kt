package com.tendai.musicx.ui.album

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tendai.common.ARTISTS_ROOT
import com.tendai.common.ClientServiceConnection
import com.tendai.common.EXTRA_ALBUM_ID
import com.tendai.common.IS_ALBUM
import com.tendai.musicx.model.MediaItemData
import javax.inject.Inject

class AlbumDetailsViewModel @Inject constructor(private val connection: ClientServiceConnection) :
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
            _tracksInAlbum.postValue(items)
        }
    }

    private val _tracksInAlbum = MutableLiveData<List<MediaItemData>>()
    val tracksInAlbum: LiveData<List<MediaItemData>> = _tracksInAlbum

    fun subscribe(albumId: Long) {
        val options = Bundle().apply {
            putBoolean(IS_ALBUM, true)
            putLong(EXTRA_ALBUM_ID, albumId)
        }
        connection.subscribe(ARTISTS_ROOT, options, subscriptionCallback)
    }
}