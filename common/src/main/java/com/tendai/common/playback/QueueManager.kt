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
    private var playingQueue = mutableListOf<MediaSessionCompat.QueueItem?>()

    @Volatile
    var currentIndex = 0

    fun getCurrentQueueItem(): MediaSessionCompat.QueueItem? {
        TODO("Not yet implemented")
    }


    fun buildQueue(trackId: Long, extras: Bundle) {
        var metadatas = listOf<MediaMetadataCompat>()
        serviceScope.launch {
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
            playingQueue = metadatas.mapIndexed { index, mediaMetadataCompat ->
                if (trackId == mediaMetadataCompat.description.mediaId?.toLong()) {
                    currentIndex = index
                }
                mediaMetadataCompat.description.mediaId?.let { mediaId ->
                    MediaSessionCompat.QueueItem(mediaMetadataCompat.description, mediaId.toLong())
                }
            }.toMutableList()
        }
    }
}


