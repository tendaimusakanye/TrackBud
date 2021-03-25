package com.tendai.common.playback

import android.support.v4.media.session.PlaybackStateCompat

class PlaybackManager(
    private val playback: Playback,
    private val queueManager: QueueManager
) {
    lateinit var onPlaybackStart: () -> Unit
    lateinit var onPlaybackStop: () -> Unit
    lateinit var onPlaybackPause: () -> Unit
    lateinit var onPlaybackStateChanged: (playbackState: PlaybackStateCompat) -> Unit

    val mediaSessionCallback: MediaSessionCallback
        get() = MediaSessionCallback(this, playback, queueManager)


    fun updatePlaybackState() {

    }

    fun handlePlayRequest(trackId: Long) {
        if (trackId != queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()) {
            queueManager.onMetadataChanged(queueManager.getMetadata(trackId))
        }
        updatePlaybackState()
        onPlaybackStart()
        playback.playFromId(trackId)
    }

    fun handlePreviousOrNextRequest(amount: Int) {
        when (queueManager.getShuffleMode()) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToQueueItem(amount)
            PlaybackStateCompat.SHUFFLE_MODE_GROUP -> {
                if (amount == 1) {
                    queueManager.getNextShuffleIndex()
                } else queueManager.getPreviousShuffleIndex()
            }
        }
        val item = queueManager.getCurrentItemPlaying()
        val trackId = item?.description?.mediaId?.toLong()

        queueManager.onMetadataChanged(queueManager.getMetadata(trackId!!))
        updatePlaybackState()
        playback.playFromId(trackId)
    }

    fun onPlaybackStarted(playbackStart: () -> Unit) {
        this.onPlaybackStart = playbackStart
    }

    fun onPlaybackPaused(playbackPause: () -> Unit) {
        this.onPlaybackPause = playbackPause
    }

    fun onPlaybackStateUpdated(playbackStateUpdated: (playbackState: PlaybackStateCompat) -> Unit) {
        this.onPlaybackStateChanged = playbackStateUpdated
    }

    fun onPlaybackStopped(playbackStop: () -> Unit) {
        this.onPlaybackStop = playbackStop
    }

    private fun getAvailableActions() {

    }
}

