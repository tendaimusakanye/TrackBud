package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.*
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias MetadataChangedListener = (metadata: MediaMetadataCompat) -> Unit

class QueueManager(
    private val serviceScope: CoroutineScope,
    private val mediaSession: MediaSessionCompat,
    private val trackRepository: Repository.Tracks
) {
    @Volatile
    private var playingQueue = mutableListOf<MediaSessionCompat.QueueItem?>()

    @Volatile
    var currentIndex = 0
    private lateinit var onMetadataChanged: MetadataChangedListener

    fun buildQueue(trackId: Long, extras: Bundle) {

        var metadatas = listOf<MediaMetadataCompat>()
        serviceScope.launch {
            val trackMetadata = trackRepository.getTrackDetails(trackId.toInt())
            when {
                extras.getBoolean(TRACKS_ROOT) -> {
                    metadatas = trackRepository.getTracks()
                }
                extras.getBoolean(IS_ALBUM) -> {
                    metadatas = trackRepository.getTracksInAlbum(extras.getInt(EXTRA_ALBUM_ID))
                }
                extras.getBoolean(IS_ARTIST_TRACKS) -> {
                    metadatas = trackRepository.getTracksForArtist(extras.getInt(EXTRA_ARTIST_ID))
                }
                extras.getBoolean(IS_PLAYLIST) -> {
                    metadatas =
                        trackRepository.getTracksInPlaylist(extras.getInt(EXTRA_PLAYLIST_ID))
                }
            }

            //should I move this to a different thread ?
            playingQueue = metadatas.mapIndexed { index, metadata ->
                val mediaId = metadata.description.mediaId?.toLong()
                if (trackId == mediaId) currentIndex = index

                mediaId?.let {
                    MediaSessionCompat.QueueItem(metadata.description, it)
                }
            }.toMutableList()
            updateMetadata()
        }
    }

    fun onMetadataChanged(onMetadataChanged: MetadataChangedListener) {
        this.onMetadataChanged = onMetadataChanged
    }

    private suspend fun updateMetadata() {
        val currentTrack = playingQueue[currentIndex]
        val trackId = currentTrack?.description?.mediaId?.toInt()
        val trackMetadata = trackId?.let {
            withContext(serviceScope.coroutineContext) {
                trackRepository.getTrackDetails(trackId)
            }
        }
        trackMetadata?.let { onMetadataChanged(it) }
//        this.onMetadataChanged()
    }
}


//todo: First complete all the functionalities, then optimize the code later
//i.e. caching, paging, code structure, data structures etc. etc.
//todo: cache the metadatas or only access coroutines when the queue has stale data.