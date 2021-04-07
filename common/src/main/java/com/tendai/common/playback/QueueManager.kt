package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.*
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*


class QueueManager(
    private val serviceScope: CoroutineScope,
    private val trackRepository: Repository.Tracks,
    val mediaSession: MediaSessionCompat
) {
    private val slidingWindow = SlidingWindow()

    @Volatile
    var currentIndex = 0
    var cachedTrackId = 0L

    @Volatile
    var playingQueue = mutableListOf<MediaSessionCompat.QueueItem?>()
    var currentQueueTitle: CharSequence? = ""

    lateinit var onMetadataChanged: (metadata: MediaMetadataCompat) -> Unit
    lateinit var onQueueChanged: (title: CharSequence, queueItems: List<MediaSessionCompat.QueueItem?>) -> Unit

    fun buildQueue(trackId: Long, extras: Bundle) {
        if (isTrackAlreadyInQueue(trackId)) return

        serviceScope.launch {
            var metadatas = listOf<MediaMetadataCompat>()
            var queueTitle: CharSequence? = ""
            when {
                extras.getBoolean(TRACKS_ROOT) -> {
                    metadatas = trackRepository.getTracks()
                    queueTitle = "Tracks"
                }
                extras.getBoolean(IS_ALBUM) -> {
                    metadatas = trackRepository.getTracksInAlbum(extras.getLong(EXTRA_ALBUM_ID))
                    queueTitle = metadatas[0].description.description
                }
                extras.getBoolean(IS_ARTIST_TRACKS) -> {
                    metadatas = trackRepository.getTracksForArtist(extras.getLong(EXTRA_ARTIST_ID))
                    queueTitle = metadatas[0].description.subtitle
                }
                extras.getBoolean(IS_PLAYLIST) -> {
                    metadatas =
                        trackRepository.getTracksInPlaylist(extras.getLong(EXTRA_PLAYLIST_ID))
                    queueTitle = metadatas[0].description.subtitle
                }
            }
            if (playingQueue.isNotEmpty()) {
                playingQueue.clear()
            }
            metadatas.forEachIndexed { index, metadata ->
                val mediaId = metadata.description.mediaId?.toLong()
                if (trackId == mediaId) currentIndex = index

                mediaId?.let {
                    playingQueue.add(
                        MediaSessionCompat.QueueItem(
                            metadata.description,
                            it
                        )
                    )
                }
            }
            slidingWindow.createWindow()
            currentQueueTitle = queueTitle
            onQueueChanged(
                queueTitle!!, playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
        }
    }

    fun getMetadata(trackId: Long): MediaMetadataCompat {
        var metadata: MediaMetadataCompat? = null
        serviceScope.launch {
            metadata = trackRepository.getTrackDetails(trackId)
        }
        return metadata!!
    }

    fun getCurrentItemPlaying(): MediaSessionCompat.QueueItem? = playingQueue[currentIndex]

    fun setCurrentQueueItem(id: Long) {
        playingQueue.forEachIndexed { index, queueItem ->
            queueItem?.let {
                if (queueItem.queueId == id) {
                    currentIndex = index
                }
            }
        }
    }

    fun skipToNext() {
        //loop again when we have reached the end of the queue and adjust accordingly
        if (currentIndex == playingQueue.size - 1) {
            currentIndex = 0
            slidingWindow.createWindow()
            onQueueChanged(
                currentQueueTitle!!, playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
        } else {
            currentIndex += 1
            if (slidingWindow.advance()) onQueueChanged(
                currentQueueTitle!!, playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
        }
    }

    fun skipToPrevious() {
        if (currentIndex == 0) {
            currentIndex = playingQueue.size - 1
            slidingWindow.createWindow()
            onQueueChanged(
                currentQueueTitle!!, playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
        } else {
            currentIndex -= 1
            if (slidingWindow.shrink()) onQueueChanged(
                currentQueueTitle!!, playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
        }
    }

    fun shuffleToNext() {

    }

    fun shuffleToPrevious() {

    }

    private fun isTrackAlreadyInQueue(trackId: Long): Boolean {
        if (cachedTrackId == trackId) return true

        if (playingQueue.isNotEmpty()) {
            playingQueue.forEachIndexed { index, queueItem ->
                if (queueItem?.description?.mediaId?.toLong() == trackId) {
                    cachedTrackId = trackId
                    currentIndex = index
                    slidingWindow.createWindow()

                    onQueueChanged(
                        currentQueueTitle!!,
                        playingQueue.subList(slidingWindow.start, slidingWindow.end)
                    )
                    return true
                }
            }
        }
        return false
    }

    fun onMetadataChangedListener(metadataChanged: (metadata: MediaMetadataCompat) -> Unit) {
        onMetadataChanged = metadataChanged
    }

    fun onQueueChangedListener(
        queueUpdated: (title: CharSequence, queueItems: List<MediaSessionCompat.QueueItem?>) -> Unit
    ) {
        onQueueChanged = queueUpdated
    }

    private inner class SlidingWindow {
        var start: Int = 0
        var end: Int = 0
        private var queueWindow = ArrayDeque<Int>(WINDOW_CAPACITY)

        fun advance(): Boolean {
            var temp = currentIndex
            if (currentIndex > queueWindow.last) {
                var i = 0
                if (playingQueue.size % WINDOW_CAPACITY == 0) {
                    while (i++ < WINDOW_CAPACITY) {
                        queueWindow.pollFirst()
                        queueWindow.offerLast(temp++)
                    }
                } else {
                    val difference: Int = playingQueue.size - queueWindow.size
                    while (i++ < difference) {
                        queueWindow.pollFirst()
                        queueWindow.offerLast(temp++)
                    }
                }
                updateIndices()
                return true
            }
            return false
        }

        fun shrink(): Boolean {
            var temp = currentIndex
            if (currentIndex < queueWindow.first) {
                var i = 0
                if (playingQueue.size % WINDOW_CAPACITY == 0) {
                    while (i++ < WINDOW_CAPACITY) {
                        queueWindow.pollLast()
                        queueWindow.offerFirst(temp--)
                    }
                } else {
                    val difference: Int = playingQueue.size - queueWindow.size
                    while (i++ < difference) {
                        queueWindow.pollLast()
                        queueWindow.offerFirst(temp--)
                    }
                }
                updateIndices()
                return true
            }
            return false
        }

        fun updateIndices() {
            start = queueWindow.first
            //the subList end index is exclusive hence the plus one
            end = queueWindow.last + 1
        }

        fun createWindow() {
            if (currentIndex > playingQueue.size - 1 || currentIndex < 0) return  // if false proceed down.
            queueWindow.clear()
            var tempIndex = currentIndex

            for (i in 0 until WINDOW_CAPACITY) {
                if (currentIndex == playingQueue.size - 1) {
                    if (tempIndex >= 0) queueWindow.offerFirst(tempIndex--)
                } else {
                    if (tempIndex < playingQueue.size) queueWindow.offerLast(tempIndex++) else {
                        var currentFirst = queueWindow.first
                        if (--currentFirst >= 0) queueWindow.offerFirst(currentFirst)
                    }
                }
            }
            updateIndices()
        }
    }
}

const val WINDOW_CAPACITY = 16