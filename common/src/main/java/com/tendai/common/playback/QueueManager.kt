package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.*
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class QueueManager(
    private val serviceScope: CoroutineScope,
    private val mediaSession: MediaSessionCompat,
    private val trackRepository: Repository.Tracks
) {
    @Volatile
    var playingQueue = mutableListOf<MediaSessionCompat.QueueItem?>()

    @Volatile
    var currentIndex = 0
    lateinit var onMetadataChanged: (metadata: MediaMetadataCompat) -> Unit
    lateinit var onQueueChanged: (title: CharSequence, queueItems: List<MediaSessionCompat.QueueItem?>) -> Unit

    fun buildQueue(trackId: Long, extras: Bundle) {
        var metadatas = listOf<MediaMetadataCompat>()
        serviceScope.launch {
            val trackMetadata = trackRepository.getTrackDetails(trackId)
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
            playingQueue = metadatas.mapIndexed { index, metadata ->
                val mediaId = metadata.description.mediaId?.toLong()
                if (trackId == mediaId) currentIndex = index

                mediaId?.let { MediaSessionCompat.QueueItem(metadata.description, it) }
            }.toMutableList()

            //todo: what happens when the queue reaches position 15
            val queueList = if (playingQueue.size < 15) {
                playingQueue.subList(0, playingQueue.size - 1).toList()
            } else {
                playingQueue.subList(0, 15).toList()
            }
//            onMetadataChanged(trackMetadata)
            onQueueChanged(queueTitle!!, queueList)
        }
    }

    fun getCurrentItemPlaying(): MediaSessionCompat.QueueItem? = playingQueue[currentIndex]

    fun getRepeatMode(): Int = mediaSession.controller.repeatMode

    fun getShuffleMode(): Int = mediaSession.controller.shuffleMode

    fun getMetadata(trackId: Long): MediaMetadataCompat {
        var metadata: MediaMetadataCompat? = null
        serviceScope.launch {
            metadata = trackRepository.getTrackDetails(trackId)
        }
        return metadata!!
    }

    // add more logic to this method.
    //todo: check if index is the last index given the repeat mode and shuffleMode are none. then loop again.
    fun skipToQueueItem(amount: Int) {
        val index = currentIndex + amount

        if (index < 0 && index > playingQueue.size) {
            throw IndexOutOfBoundsException(" Index is not valid")
        } else {
            currentIndex = index
        }
    }

    fun getNextShuffleIndex() {
        TODO("Generate random indexes which loop through each node in the list once")
    }

    fun getPreviousShuffleIndex() {
        TODO("Not yet implemented")
    }

    fun onMetadataChangedListener(metadataChanged: (metadata: MediaMetadataCompat) -> Unit) {
        onMetadataChanged = metadataChanged
    }

    fun onQueueChangedListener(
        queueUpdated: (title: CharSequence, queueItems: List<MediaSessionCompat.QueueItem?>) -> Unit
    ) {
        onQueueChanged = queueUpdated
    }

}


//todo: First complete all the functionality, then optimize the code later
//todo: this class has too much logic.
//i.e. caching, paging, code structure, data structures etc. etc.
//todo: cache the metadatas or only access coroutines when the queue has stale data.