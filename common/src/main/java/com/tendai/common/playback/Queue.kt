package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.*
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Queue(
    serviceScope: CoroutineScope,
    trackRepository: Repository.Tracks
) : QueueManager(serviceScope, trackRepository) {

    private var cachedTrackId = 0L
    private var currentQueueTitle: CharSequence? = ""

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

    override fun skipToPrevious() {
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

    override fun shuffleToNext() {

    }

    override fun shuffleToPrevious() {

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

}