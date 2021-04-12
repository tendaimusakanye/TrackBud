package com.tendai.common.playback

import android.media.AudioManager
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

class PlaybackManager(
    val mediaSession: MediaSessionCompat,
    private val playback: Playback,
    private val queueManager: QueueManager
) : Callback {

    val mediaSessionCallback: MediaSessionCallback
        get() = MediaSessionCallback()
    private val stateBuilder = PlaybackStateCompat.Builder().setActions(getAvailableActions())
        .setState(PlaybackStateCompat.STATE_NONE, 0L, 1.0F)

    lateinit var onMetadataChanged: (metadata: MediaMetadataCompat) -> Unit
    private lateinit var onNotificationRequired: (state: Int) -> Unit
    private lateinit var onPlaybackStart: () -> Unit
    private lateinit var onPlaybackStop: () -> Unit
    private lateinit var onPlaybackPause: () -> Unit
    private lateinit var onPlaybackStateChanged: (playbackState: PlaybackStateCompat) -> Unit

    override fun onCompletion() {
        when (mediaSession.controller.repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_ONE -> repeatTrack()
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                queueManager.skipToNext()
                handlePreviousOrNextRequest(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
            }
            PlaybackStateCompat.REPEAT_MODE_GROUP -> {
                if (mediaSession.controller.shuffleMode
                    == PlaybackStateCompat.SHUFFLE_MODE_NONE
                ) {
                    queueManager.skipToNext()
                    handlePreviousOrNextRequest(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
                } else if (mediaSession.controller.shuffleMode
                    == PlaybackStateCompat.SHUFFLE_MODE_GROUP
                ) {
                    queueManager.shuffleToNext()
                    handlePreviousOrNextRequest(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
                }
            }
        }
    }


    override fun onPrepared() {
        updatePlaybackState()
    }

    fun updatePlaybackState() {
        var position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
        val state = playback.getState()

        if (state == PlaybackStateCompat.STATE_PLAYING
            || state == PlaybackStateCompat.STATE_PAUSED
            || state == PlaybackStateCompat.STATE_STOPPED
        ) {
            position = playback.getCurrentPosition().toLong()
        }
        stateBuilder.setState(state, position, 1.0F, SystemClock.elapsedRealtime())
        queueManager.getCurrentItemPlaying()?.queueId?.let { stateBuilder.setActiveQueueItemId(it) }

        onPlaybackStateChanged(stateBuilder.build())
        onNotificationRequired(state)
    }

    fun cleanUp() {
        playback.release()
        updatePlaybackState()
    }

    private fun handlePlayRequest(trackId: Long) {
        if (playback.requestFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            onPlaybackStart()
            with(queueManager) {
                onMetadataChanged(getMetadata(trackId))
            }
            playback.playFromId(trackId)
        } else {
            Log.e(TAG, "Failed to get AudioFocus")
        }
    }

    private fun handlePreviousOrNextRequest(state: Int) {
        val newState = stateBuilder.setState(
            state,
            mediaSession.controller.playbackState.position,
            1.0F
        )
        onPlaybackStateChanged(newState.build())
        val trackId = queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()
        handlePlayRequest(trackId!!)
    }

    private fun repeatTrack() {
        val trackId = queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()
        handlePlayRequest(trackId!!)
    }

    private fun setRepeatOrShuffleMode(shuffleOrRepeatMode: Int, isRepeatMode: Boolean) {
        val bundle = mediaSession.controller.extras ?: Bundle()
        onPlaybackStateChanged(
            PlaybackStateCompat.Builder(mediaSession.controller.playbackState)
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

    //callbacks.
    fun onNotificationRequiredListener(notificationRequired: (state: Int) -> Unit) {
        onNotificationRequired = notificationRequired
    }

    fun onPlaybackStarted(playbackStart: () -> Unit) {
        onPlaybackStart = playbackStart
    }

    fun onPlaybackPaused(playbackPause: () -> Unit) {
        onPlaybackPause = playbackPause
    }

    fun onMetadataChangedListener(metadataChanged: (metadata: MediaMetadataCompat) -> Unit) {
        onMetadataChanged = metadataChanged
    }

    fun onPlaybackStateUpdated(playbackStateUpdated: (playbackState: PlaybackStateCompat) -> Unit) {
        onPlaybackStateChanged = playbackStateUpdated
    }

    fun onPlaybackStopped(playbackStop: () -> Unit) {
        onPlaybackStop = playbackStop
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onSeekTo(pos: Long) {
            playback.seekTo(pos.toInt())
            updatePlaybackState()
        }

        override fun onPlay() {
            val trackId = queueManager.getCurrentItemPlaying()?.description?.mediaId?.toLong()
            handlePlayRequest(trackId!!)
        }

        override fun onSkipToQueueItem(id: Long) {
            val newState = stateBuilder.setState(
                PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM,
                mediaSession.controller.playbackState.position,
                1.0F
            )
            onPlaybackStateChanged(newState.build())

            with(queueManager) {
                setCurrentQueueItem(id)
                val trackId = getCurrentItemPlaying()?.description?.mediaId?.toLong()
                this@PlaybackManager.handlePlayRequest(trackId!!)
            }
        }

        override fun onSkipToPrevious() {
            when (mediaSession.controller.shuffleMode) {
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToPrevious()
                PlaybackStateCompat.SHUFFLE_MODE_GROUP -> {
                    queueManager.shuffleToPrevious()
                }
            }
            handlePreviousOrNextRequest(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
        }

        override fun onStop() {
            playback.stop()
            onPlaybackStop()
            updatePlaybackState()
        }

        override fun onSkipToNext() {
            when (mediaSession.controller.shuffleMode) {
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToNext()
                PlaybackStateCompat.SHUFFLE_MODE_GROUP -> queueManager.shuffleToNext()
            }
            handlePreviousOrNextRequest(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
        }

        override fun onPause() {
            if (playback.isPlaying()) {
                playback.pause()
                onPlaybackPause()
                updatePlaybackState()
            }
        }

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
private const val TAG = "PlaybackManager"

//todo: write tests for testing errors  in logic, exceptions , null pointers and log
// accordingly for debugging purposes. etc, etc.