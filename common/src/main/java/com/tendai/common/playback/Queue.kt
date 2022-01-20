package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.*
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

class Queue @Inject constructor(
    private val serviceScope: CoroutineScope,
    private val trackRepository: Repository.Tracks
) : QueueManager() {

    val slidingWindow = SlidingWindow()
    val playingQueue = mutableListOf<MediaSessionCompat.QueueItem?>()

    private var currentQueueTitle: CharSequence? = ""
    private var nextShuffleIndexCount = 0
    private var previousShuffleIndexCount = 0

    var currentIndex: Int = -1
        private set

    override fun buildQueue(trackId: Long, extras: Bundle) {
        if (isTrackAlreadyInQueue(trackId)) return

        serviceScope.launch {
            var tracksMetadata = listOf<MediaMetadataCompat>()
            var queueTitle: CharSequence? = ""
            when {
                extras.getBoolean(TRACKS_ROOT) -> {
                    tracksMetadata = trackRepository.getTracks()
                    queueTitle = "Tracks"
                }
                extras.getBoolean(IS_ALBUM) -> {
                    tracksMetadata = trackRepository.getTracksInAlbum(extras.getLong(EXTRA_ALBUM_ID))
                    queueTitle = tracksMetadata[0].description.description
                }
                extras.getBoolean(IS_ARTIST_TRACKS) -> {
                    tracksMetadata = trackRepository.getTracksForArtist(extras.getLong(EXTRA_ARTIST_ID))
                    queueTitle = tracksMetadata[0].description.subtitle
                }
                extras.getBoolean(IS_PLAYLIST) -> {
                    tracksMetadata =
                        trackRepository.getTracksInPlaylist(extras.getLong(EXTRA_PLAYLIST_ID))
                    queueTitle = tracksMetadata[0].description.subtitle
                }
            }
            if (playingQueue.isNotEmpty()) {
                playingQueue.clear()
            }
            tracksMetadata.distinctBy { it.description.title }.forEachIndexed { index, metadata ->
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
        if (currentIndex >= playingQueue.lastIndex) {
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
            currentIndex = playingQueue.lastIndex
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
        val shuffledList = slidingWindow.shuffledList
        var index = shuffledList.indexOf(currentIndex)

        if (nextShuffleIndexCount < WINDOW_CAPACITY && !slidingWindow.refreshWindow(FLAG_NEXT)) {
            if (index == shuffledList.lastIndex) {
                index = 0
                currentIndex = shuffledList[index]
            } else {
                currentIndex = shuffledList[++index]
            }
            nextShuffleIndexCount++
            if (previousShuffleIndexCount != 0) previousShuffleIndexCount--
        } else {
            nextShuffleIndexCount = 0

            with(shuffledList) {
                currentIndex = maxOrNull()!!
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

    // Add comments.//javadoc for these methods.
    override fun shuffleToPrevious() {
        val shuffledList = slidingWindow.shuffledList
        var index = shuffledList.indexOf(currentIndex)

        if (previousShuffleIndexCount < WINDOW_CAPACITY && !slidingWindow.refreshWindow(
                FLAG_PREVIOUS
            )
        ) {
            if (index == 0) {
                index = shuffledList.lastIndex
                currentIndex = shuffledList[index]
            } else {
                currentIndex = shuffledList[--index]
            }

            previousShuffleIndexCount++
            if (nextShuffleIndexCount != 0) nextShuffleIndexCount--
        } else {
            previousShuffleIndexCount = 0

            val min = shuffledList.minOrNull()!!
            currentIndex = if (min != 0)
                min - WINDOW_CAPACITY
            else
                playingQueue.lastIndex


            slidingWindow.createWindow()
            slidingWindow.createShuffleList()
            onQueueChanged(
                currentQueueTitle!!,
                playingQueue.subList(slidingWindow.start, slidingWindow.end)
            )
            shuffleToPrevious()
        }
    }

    fun isTrackAlreadyInQueue(trackId: Long): Boolean {
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
    inner class SlidingWindow {
        var start: Int = 0
        var end: Int = 0

        internal lateinit var shuffledList: List<Int>
            private set

        private var queueWindow = ArrayDeque<Int>(WINDOW_CAPACITY)

        fun advance(): Boolean {
            if (currentIndex > queueWindow.last) {
                var temp = currentIndex
                var i = 0
                if (playingQueue.size % WINDOW_CAPACITY == 0) {
                    while (i++ < WINDOW_CAPACITY) {
                        queueWindow.pollFirst()
                        queueWindow.offerLast(temp++)
                    }
                } else {
                    val difference = playingQueue.size % WINDOW_CAPACITY
                    while (i++ < difference) {
                        if (temp > playingQueue.lastIndex) temp = 0
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
            if (currentIndex < queueWindow.first) {
                var temp = currentIndex
                var i = 0
                if (playingQueue.size % WINDOW_CAPACITY == 0) {
                    while (i++ < WINDOW_CAPACITY) {
                        queueWindow.pollLast()
                        queueWindow.offerFirst(temp--)
                    }
                } else {
                    val difference = playingQueue.size % WINDOW_CAPACITY
                    while (i++ < difference) {
                        if (temp < 0) temp = playingQueue.lastIndex
                        queueWindow.pollLast()
                        queueWindow.offerFirst(temp--)
                    }
                }
                updateIndices()
                return true
            }
            return false
        }

        // create the window only if the currentIndex is greater than the last index or if it is <= -1
        fun createWindow(): Boolean {
            if (currentIndex > playingQueue.lastIndex || currentIndex <= -1) return false
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

        fun refreshWindow(flag: CharSequence): Boolean {
            return if (flag == FLAG_NEXT) {
                currentIndex == queueWindow.last && nextShuffleIndexCount == 0
            } else {
                currentIndex == queueWindow.first && previousShuffleIndexCount == 16
            }
        }

        fun createShuffleList() {
            val tempWindow = queueWindow
            shuffledList = tempWindow.toList().shuffled()
        }

        private fun updateIndices() {
            start = queueWindow.first
            end = queueWindow.last + 1 //the subList end index is exclusive hence the plus one
        }
    }
}

private const val WINDOW_CAPACITY = 16
private const val FLAG_NEXT = "FLAG_NEXT"
private const val FLAG_PREVIOUS = "FLAG_PREVIOUS"


//TODO: 12/29/21 Add comments and java-doc for this class
// TODO: 1/14/22 Clarify changes made in the SlidingWindow and Queue class methods by properly documenting the desired functionality
// TODO: 1/19/22 List or HashMap for playingQueue


