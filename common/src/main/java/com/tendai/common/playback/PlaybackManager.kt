package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat


abstract class PlaybackManager(
    val serviceCallback: PlaybackServiceCallback,
    val playback: Playback,
    val queueManager: QueueManager
) {

    val mediaSessionCallback: MediaSessionCallback
        get() = MediaSessionCallback()

    fun play() {
        playback.play()
    }

    fun play(trackId: Long, extras: Bundle) {

    }

    fun onCompletion() {

    }

    fun updatePlaybackState() {

    }

    fun getAvailableActions() {

    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        override fun onSeekTo(pos: Long) {
            playback.seekTo(pos)
        }

        override fun onSkipToPrevious() {

        }

        override fun onStop() {
            playback.stop()
        }

        override fun onSkipToNext() {
        }

        override fun onPause() {
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            val trackId = mediaId!!.toLong()
            queueManager.buildQueue(trackId, extras!!)
            serviceCallback.onPlaybackStart()
            playback.playFromId(trackId)
        }
    }

}

interface PlaybackServiceCallback {
    fun onPlaybackStart()
    fun onPlaybackStop()
    fun onNotificationRequired()
    fun onPlaybackStateUpdated(newState: PlaybackStateCompat)
}