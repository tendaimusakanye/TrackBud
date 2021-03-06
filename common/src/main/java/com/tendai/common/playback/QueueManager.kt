package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat

abstract class QueueManager {
    var cachedTrackId = -1L

    protected lateinit var onQueueChanged: (title: CharSequence, queueItems: List<MediaSessionCompat.QueueItem?>) -> Unit
        private set

    abstract fun buildQueue(trackId: Long, extras: Bundle)
    abstract fun createShuffleWindow()
    abstract fun getMetadata(trackId: Long): MediaMetadataCompat
    abstract fun getCurrentItemPlaying(): MediaSessionCompat.QueueItem?
    abstract fun setCurrentQueueItem(id: Long)
    abstract fun skipToNext()
    abstract fun skipToPrevious()
    abstract fun shuffleToNext()
    abstract fun shuffleToPrevious()

    //callback
    fun onQueueChangedListener(
        queueUpdated: (title: CharSequence, queueItems: List<MediaSessionCompat.QueueItem?>) -> Unit
    ) {
        onQueueChanged = queueUpdated
    }
}

