package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class PlaybackManager(
    private val playback: Playback,
    private val queueManager: QueueManager
) : Callback {
    val mediaSessionCallback: MediaSessionCallback
        get() = MediaSessionCallback()

    private lateinit var onNotificationRequired: (state: Int) -> Unit
    private lateinit var onPlaybackStarted: () -> Unit
    private lateinit var onPlaybackStopped: () -> Unit
    private lateinit var onPlaybackPaused: () -> Unit
    private lateinit var onPlaybackStateChanged: (playbackState: PlaybackStateCompat) -> Unit

    override fun onCompletion() {
        when (queueManager.mediaSession.controller.repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_ONE -> repeatTrack()
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                queueManager.skipToNext()
                handlePreviousOrNextRequest()
            }
            PlaybackStateCompat.REPEAT_MODE_GROUP -> {
                if (queueManager.mediaSession.controller.shuffleMode
                    == PlaybackStateCompat.SHUFFLE_MODE_NONE
                ) {
                    queueManager.skipToNext()
                    handlePreviousOrNextRequest()
                } else if (queueManager.mediaSession.controller.shuffleMode
                    == PlaybackStateCompat.SHUFFLE_MODE_GROUP
                ) {
                    queueManager.shuffleToNext()
                }
            }
        }
    }

    fun updatePlaybackState() {

    }

    private fun handlePlayRequest(trackId: Long) {
        onPlaybackStarted()
        if (trackId != queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()) {
            queueManager.onMetadataChanged(queueManager.getMetadata(trackId))
        }
        updatePlaybackState()
        playback.playFromId(trackId)
        onNotificationRequired(playback.getState())
    }

    private fun handlePreviousOrNextRequest() {
        val trackId = queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()
        queueManager.onMetadataChanged(queueManager.getMetadata(trackId!!))
        updatePlaybackState()
        playback.playFromId(trackId)
    }

    private fun repeatTrack() {
        val trackId = queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()
        updatePlaybackState()
        playback.playFromId(trackId!!)
    }

    private fun repeatQueue() {
        when (queueManager.mediaSession.controller.shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToNext()
            PlaybackStateCompat.SHUFFLE_MODE_GROUP -> {
                //todo: shuffleToNext should handle this like skipToNext ?
            }
        }
    }

    private fun setRepeatOrShuffleMode(shuffleOrRepeatMode: Int, isRepeatMode: Boolean) {
        val bundle = queueManager.mediaSession.controller.extras ?: Bundle()
        onPlaybackStateChanged(
            PlaybackStateCompat.Builder(queueManager.mediaSession.controller.playbackState)
                .setExtras(bundle.apply {
                    if (isRepeatMode) putInt(REPEAT_MODE, shuffleOrRepeatMode)
                    else putInt(SHUFFLE_MODE, shuffleOrRepeatMode)
                }).build()
        )
    }

    private fun getAvailableActions(): Long {
        var actions = PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE

        actions = if (playback.isPlaying()) {
            actions or PlaybackStateCompat.ACTION_PAUSE
        } else {
            actions or PlaybackStateCompat.ACTION_PLAY
        }
        return actions
    }

    fun onNotificationRequiredListener(notificationRequired: (state: Int) -> Unit) {
        onNotificationRequired = notificationRequired
    }

    fun onPlaybackStartedListener(playbackStart: () -> Unit) {
        onPlaybackStarted = playbackStart
    }

    fun onPlaybackPausedListener(playbackPause: () -> Unit) {
        onPlaybackPaused = playbackPause
    }

    fun onPlaybackStateUpdatedListener(playbackStateUpdated: (playbackState: PlaybackStateCompat) -> Unit) {
        onPlaybackStateChanged = playbackStateUpdated
    }

    fun onPlaybackStoppedListener(playbackStop: () -> Unit) {
        onPlaybackStopped = playbackStop
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onSeekTo(pos: Long) {
            playback.seekTo(pos)
            updatePlaybackState()
        }

        override fun onPlay() {
            val trackId = queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()
            handlePlayRequest(trackId!!)
        }

        override fun onSkipToQueueItem(id: Long) {
            queueManager.run {
                setCurrentQueueItem(id)
                val trackId = getCurrentItemPlaying()?.description?.mediaId?.toLong()
                onMetadataChanged(getMetadata(trackId!!))
                this@PlaybackManager.handlePlayRequest(trackId)
            }
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            when (action) {
                ACTION_REPEAT_SONG -> repeatTrack()
                ACTION_REPEAT_GROUP -> repeatQueue()
            }
        }

        override fun onSkipToPrevious() {
            when (queueManager.mediaSession.controller.shuffleMode) {
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToPrevious()
                PlaybackStateCompat.SHUFFLE_MODE_GROUP -> {
                    queueManager.shuffleToPrevious()
                }
            }
            handlePreviousOrNextRequest()
        }

        override fun onStop() {
            playback.stop()
            onPlaybackStopped()
            updatePlaybackState()
        }

        override fun onSkipToNext() {
            when (queueManager.mediaSession.controller.shuffleMode) {
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToNext()
                PlaybackStateCompat.SHUFFLE_MODE_GROUP -> {
                    queueManager.shuffleToNext()
                }
            }
            handlePreviousOrNextRequest()
        }

        override fun onPause() {
            if (playback.isPlaying()) {
                playback.pause()
                onPlaybackPaused()
                updatePlaybackState()
            }
        }

        //todo:  see if invoking super here has any effect or not ?
        override fun onSetShuffleMode(shuffleMode: Int) {
            setRepeatOrShuffleMode(shuffleMode, false)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            setRepeatOrShuffleMode(repeatMode, true)
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            val trackId = mediaId!!.toLong()
            queueManager.buildQueue(trackId, extras!!)
            handlePlayRequest(trackId)
        }
    }

}

const val REPEAT_MODE = "com.tendai.common.playback.REPEAT_MODE"
const val SHUFFLE_MODE = "com.tendai.common.playback.SHUFFLE_MODE"
const val ACTION_REPEAT_SONG = "com.tendai.common.playback.ACTION_REPEAT_SONG"
const val ACTION_REPEAT_GROUP = "com.tendai.common.playback.ACTION_REPEAT_GROUP"


