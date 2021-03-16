package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

typealias PlaybackStartCallback = () -> Unit
typealias PlaybackStopCallback = () -> Unit
typealias PlaybackStateChangedCallback = (newState: PlaybackStateCompat) -> Unit


class PlaybackManager(
    private val playback: Playback,
    private val queueManager: QueueManager
) {
    private lateinit var onPlaybackStart: PlaybackStartCallback

    val mediaSessionCallback: MediaSessionCallback
        get() = MediaSessionCallback()


    private fun updatePlaybackState() {

    }

    private fun getAvailableActions() {

    }

    fun onPlaybackStart(onPlaybackStart: PlaybackStartCallback) {
        this.onPlaybackStart = onPlaybackStart
    }

     inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        override fun onSeekTo(pos: Long) {
            playback.seekTo(pos)
        }

         override fun onPlay() {

         }

        override fun onCustomAction(action: String?, extras: Bundle?) {

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
            onPlaybackStart()
            playback.playFromId(trackId)
        }
    }

}

