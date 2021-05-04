package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.*
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

class Queue(
    private val serviceScope: CoroutineScope,
    private val trackRepository: Repository.Tracks
) : QueueManager() {

    private val slidingWindow = SlidingWindow()

    private var currentQueueTitle: CharSequence? = ""
    private var nextShuffleIndexCount = 0
    private var previousShuffleIndexCount = 0

    @Volatile
    private var currentIndex = 0

    @Volatile
    private var playingQueue = mutableListOf<MediaSessionCompat.QueueItem?>()

    override fun buildQueue(trackId: Long, extras: Bundle) {
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
            metadatas.distinctBy { it.description.title }.forEachIndexed { index, metadata ->
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

            if (slidingWindow.createWindow()) {
                currentQueueTitle = queueTitle
                onQueueChanged(
                    queueTitle!!, playingQueue.subList(slidingWindow.start, slidingWindow.end)
                )
            }
        }
    }

    override fun createShuffleWindow() {
        slidingWindow.createShuffleList()
    }

    override fun getMetadata(trackId: Long): MediaMetadataCompat {
        var metadata: MediaMetadataCompat? = null
        serviceScope.launch {
            metadata = trackRepository.getTrackDetails(trackId)
        }
        return metadata!!
    }

    override fun getCurrentItemPlaying(): MediaSessionCompat.QueueItem? = playingQueue[currentIndex]

    override fun setCurrentQueueItem(id: Long) {
        playingQueue.forEachIndexed { index, queueItem ->
            queueItem?.let {
                if (queueItem.queueId == id) {
                    currentIndex = index
                }
            }
        }
    }

    override fun skipToNext() {
        //loop again when we have reached the end of the queue and adjust accordingly
        if (currentIndex >= playingQueue.size - 1) {
            currentIndex = 0
            if (slidingWindow.createWindow()) {
                onQueueChanged(
                    currentQueueTitle!!,
                    playingQueue.subList(slidingWindow.start, slidingWindow.end)
                )
            }
        } else {
            currentIndex += 1
            if (slidingWindow.advance()) {
                onQueueChanged(
                    currentQueueTitle!!,
                    playingQueue.subList(slidingWindow.start, slidingWindow.end)
                )
            }
        }
    }

    override fun skipToPrevious() {
        if (currentIndex <= 0) {
            currentIndex = playingQueue.size - 1
            if (slidingWindow.createWindow()) {
                onQueueChanged(
                    currentQueueTitle!!,
                    playingQueue.subList(slidingWindow.start, slidingWindow.end)
                )
            }
        } else {
            currentIndex -= 1
            if (slidingWindow.shrink()) {
                onQueueChanged(
                    currentQueueTitle!!,
                    playingQueue.subList(slidingWindow.start, slidingWindow.end)
                )
            }
        }
    }

    override fun shuffleToNext() {
        val tempShuffleList = slidingWindow.shuffleList
        var index = tempShuffleList.indexOf(currentIndex)

        if (nextShuffleIndexCount < WINDOW_CAPACITY) {
            if (index == tempShuffleList.lastIndex) {
                index = 0
                currentIndex = tempShuffleList[index]
            } else {
                currentIndex = tempShuffleList[++index]
            }
            nextShuffleIndexCount++
            if (previousShuffleIndexCount != 0) previousShuffleIndexCount--
        } else {
            nextShuffleIndexCount = 0

            with(tempShuffleList) {
                currentIndex = maxOrNull() ?: 0
                currentIndex++
            }

            slidingWindow.createWindow()
            slidingWindow.createShuffleList()
            onQueueChanged(
                currentQueueTitle!!,
                playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
            shuffleToNext()
        }
    }

    override fun shuffleToPrevious() {
        val tempShuffleList = slidingWindow.shuffleList
        var index = tempShuffleList.indexOf(currentIndex)

        if (previousShuffleIndexCount < WINDOW_CAPACITY) {
            if (index == 0) {
                index = tempShuffleList.lastIndex
                currentIndex = tempShuffleList[index]
            } else {
                currentIndex = tempShuffleList[--index]
            }

            previousShuffleIndexCount++
            if (nextShuffleIndexCount != 0) nextShuffleIndexCount--
        } else {
            previousShuffleIndexCount = 0

            with(tempShuffleList) {
                currentIndex = minOrNull() ?: 0
                currentIndex--
            }

            slidingWindow.createWindow()
            slidingWindow.createShuffleList()
            onQueueChanged(
                currentQueueTitle!!,
                playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
            shuffleToPrevious()
        }
    }

    private fun isTrackAlreadyInQueue(trackId: Long): Boolean {
        if (cachedTrackId == trackId) return true

        if (playingQueue.isNotEmpty()) {
            playingQueue.forEachIndexed { index, queueItem ->
                if (queueItem?.description?.mediaId?.toLong() == trackId) {
                    cachedTrackId = trackId
                    currentIndex = index

                    if (slidingWindow.createWindow()) {
                        onQueueChanged(
                            currentQueueTitle!!,
                            playingQueue.subList(slidingWindow.start, slidingWindow.end)
                        )
                    }
                    return true
                }
            }
        }
        return false
    }

    /**
     * Add Java-doc for this class
     *
     */
    private inner class SlidingWindow {
        var start: Int = 0
        var end: Int = 0
        var queueWindow = ArrayDeque<Int>(WINDOW_CAPACITY)
        lateinit var shuffleList: List<Int>

        fun advance(): Boolean {
            if (currentIndex > queueWindow.last) {
                handleShrinkOrAdvance(
                    true,
                    currentIndex,
                    { queueWindow.pollFirst() },
                    { index -> queueWindow.offerLast(index) })

                updateIndices()
                return true
            }
            return false
        }

        fun shrink(): Boolean {
            if (currentIndex < queueWindow.first) {
                handleShrinkOrAdvance(
                    false,
                    currentIndex,
                    { queueWindow.pollLast() },
                    { index -> queueWindow.offerFirst(index) })

                updateIndices()
                return true
            }
            return false
        }

        fun createWindow(): Boolean {
            if (currentIndex > playingQueue.lastIndex || currentIndex < 0) return false// if false proceed down.
            queueWindow.clear()
            var tempIndex = currentIndex

            for (i in 0 until WINDOW_CAPACITY) {
                if (currentIndex == playingQueue.lastIndex) {
                    if (tempIndex >= 0) queueWindow.offerFirst(tempIndex--)
                } else {
                    if (tempIndex < playingQueue.size) {
                        queueWindow.offerLast(tempIndex++)
                    } else {
                        var currentFirst = queueWindow.first
                        if (--currentFirst >= 0) queueWindow.offerFirst(currentFirst)
                    }
                }
            }
            updateIndices()
            return true
        }

        fun createShuffleList() {
            shuffleList = queueWindow.toList().shuffled()
        }

        private fun handleShrinkOrAdvance(//could return to duplicated code with less lines.
            increment: Boolean, // Hack to increment or decrement other than using stackTrace to get the calling method name
            currentIndex: Int,
            remove: () -> Int?,
            insert: (index: Int) -> Boolean
        ) {
            var tempIndex = currentIndex
            var i = 0
            if (playingQueue.size % WINDOW_CAPACITY == 0) {
                while (i++ < WINDOW_CAPACITY) {
                    remove()
                    if (increment) insert(tempIndex++) else insert(tempIndex--)
                }
            } else {
                val difference = playingQueue.size - queueWindow.size
                while (i++ < difference) {
                    remove()
                    if (increment) insert(tempIndex++) else insert(tempIndex--)
                }
            }
        }

        private fun updateIndices() {
            start = queueWindow.first
            end = queueWindow.last + 1 //the subList end index is exclusive hence the plus one
        }
    }
}

private const val WINDOW_CAPACITY = 16

//todo: or the Kotlin windowed function for my sliding window ?
//todo: write tests for the shuffling logic.
//todo: handle the case when the use is at the end of the queue after linear traversal and enables shuffling.
// A new window should be created instead

//todo: handle case when I shuffle to another queue and then press previous again.
// I am loosing the consistency. Quick fix is expanding the queue title for now.
//

