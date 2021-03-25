package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class MediaSessionCallback(
    private val playbackManager: PlaybackManager,
    private val playback: Playback,
    private val queueManager: QueueManager
) : MediaSessionCompat.Callback() {

    override fun onSeekTo(pos: Long) {
        playback.seekTo(pos)
        playbackManager.updatePlaybackState()
    }

    override fun onPlay() {
        val item = queueManager.getCurrentItemPlaying()
        val trackId = item?.description?.mediaId?.toLong()
        playbackManager.handlePlayRequest(trackId!!)
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        when (action) {
            ACTION_REPEAT_SONG -> playbackManager.repeatTrack();
            ACTION_REPEAT_GROUP -> playbackManager.repeatQueue();
        }
    }

    override fun onSkipToPrevious() {
        when (queueManager.mediaSession.controller.shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToPrevious()
            PlaybackStateCompat.SHUFFLE_MODE_GROUP -> {
                queueManager.shuffleToPrevious()
            }
        }
        playbackManager.handlePreviousOrNextRequest()
    }

    override fun onStop() {
        playback.stop()
        playbackManager.onPlaybackStop()
        playbackManager.updatePlaybackState()
    }

    override fun onSkipToNext() {
        when (queueManager.mediaSession.controller.shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> queueManager.skipToNext()
            PlaybackStateCompat.SHUFFLE_MODE_GROUP -> {
                queueManager.shuffleToNext()
            }
        }
        playbackManager.handlePreviousOrNextRequest()
    }

    override fun onPause() {
        playbackManager.updatePlaybackState()
        playback.pause()
        playbackManager.onPlaybackPause()
    }

    //todo:  see if invoking super here has any effect or not ?
    override fun onSetShuffleMode(shuffleMode: Int) {
        playbackManager.setRepeatOrShuffleMode(shuffleMode, false)
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        playbackManager.setRepeatOrShuffleMode(repeatMode, true)
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        val trackId = mediaId!!.toLong()
        queueManager.buildQueue(trackId, extras!!)
        playbackManager.handlePlayRequest(trackId)
    }
}

const val REPEAT_MODE = "com.tendai.common.playback.REPEAT_MODE"
const val SHUFFLE_MODE = "com.tendai.common.playback.SHUFFLE_MODE"
const val ACTION_REPEAT_SONG = "com.tendai.common.playback.ACTION_REPEAT_SONG"
const val ACTION_REPEAT_GROUP = "com.tendai.common.playback.ACTION_REPEAT_GROUP"
