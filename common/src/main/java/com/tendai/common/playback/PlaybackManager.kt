package com.tendai.common.playback

import android.os.Bundle
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

    fun handlePreviousOrNextRequest() {
        val item = queueManager.getCurrentItemPlaying()
        val trackId = item?.description?.mediaId?.toLong()
        queueManager.onMetadataChanged(queueManager.getMetadata(trackId!!))
        updatePlaybackState()
        playback.playFromId(trackId)
    }

    fun repeatTrack() {

    }

    fun repeatQueue() {

    }

    fun setRepeatOrShuffleMode(shuffleOrRepeatMode: Int, isRepeatMode: Boolean) {
        val bundle = queueManager.mediaSession.controller.extras ?: Bundle()
        onPlaybackStateChanged(
            PlaybackStateCompat.Builder(queueManager.mediaSession.controller.playbackState)
                .setExtras(bundle.apply {
                    if (isRepeatMode) putInt(REPEAT_MODE, shuffleOrRepeatMode)
                    else putInt(SHUFFLE_MODE, shuffleOrRepeatMode)
                }).build()
        )
    }

    private fun getAvailableActions() {

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
}


