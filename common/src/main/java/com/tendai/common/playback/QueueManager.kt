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
    private val slidingWindow
        get() = SlidingWindow()

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

            onMetadataChanged(getMetadata(trackId))
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
        if (currentIndex == playingQueue.size - 1) {
            currentIndex = 0
            slidingWindow.createWindow()
            onQueueChanged(
                currentQueueTitle!!, playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
        } else {
            currentIndex += 1
            val queueChanged = slidingWindow.advance()
            if (queueChanged) onQueueChanged(
                currentQueueTitle!!, playingQueue.subList(
                    slidingWindow.start,
                    slidingWindow.end
                )
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
            val queueChanged = slidingWindow.shrink()
            if (queueChanged) onQueueChanged(
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
            if (currentIndex > queueWindow.last && currentIndex < playingQueue.size) {
                var i = 0
                while (i < WINDOW_CAPACITY) {
                    queueWindow.pollFirst()
                    if (temp <= playingQueue.size - 1) queueWindow.offerLast(temp++)
                    i++
                }
                updateIndices()
                return true
            }
            return false
        }

        fun shrink(): Boolean {
            var temp = currentIndex
            if (currentIndex < queueWindow.first() && currentIndex >= 0) {
                var i = 0
                while (i < WINDOW_CAPACITY) {
                    if (queueWindow.size >= WINDOW_CAPACITY) queueWindow.pollLast()
                    if (temp >= 0) queueWindow.offerFirst(temp--)
                    i++
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
            if (currentIndex > playingQueue.size - 1 || currentIndex < 0) return
            var tempIndex = currentIndex

            for (i in 0 until WINDOW_CAPACITY) {
                if (currentIndex == playingQueue.size - 1) {
                    queueWindow.offerFirst(tempIndex)
                    tempIndex--
                } else {
                    if (tempIndex >= playingQueue.size) {
                        while (queueWindow.size > WINDOW_CAPACITY - 1) queueWindow.pollFirst()
                        queueWindow.offerFirst(tempIndex - WINDOW_CAPACITY)
                    } else {
                        queueWindow.offerLast(tempIndex)
                        while (queueWindow.size > WINDOW_CAPACITY) queueWindow.pollFirst()
                        tempIndex++
                    }
                }
            }
            while (queueWindow.size > WINDOW_CAPACITY) queueWindow.pollLast()
            updateIndices()
        }
    }
}

const val WINDOW_CAPACITY = 16