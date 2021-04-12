package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import java.util.*


abstract class QueueManager(
    val serviceScope: CoroutineScope,
    val trackRepository: Repository.Tracks
) {
    protected val slidingWindow = SlidingWindow()

    @Volatile
    var currentIndex = 0
    @Volatile
    var playingQueue = mutableListOf<MediaSessionCompat.QueueItem?>()

    lateinit var onQueueChanged: (title: CharSequence, queueItems: List<MediaSessionCompat.QueueItem?>) -> Unit

    abstract fun buildQueue(trackId: Long, extras: Bundle)

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

    /**
     * Add Java-doc for this class
     */
    inner class SlidingWindow {
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

        private fun updateIndices() {
            start = queueWindow.first
            //the subList end index is exclusive hence the plus one
            end = queueWindow.last + 1
        }
    }
}

const val WINDOW_CAPACITY = 16