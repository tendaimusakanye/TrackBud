package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat

class LocalPlaybackManager(playback: Playback, queueManager: QueueManager) :
    PlaybackManager(playback, queueManager) {

    val mediaSessionCallback: LocalPlaybackManager.MediaSessionCallback
        get() = LocalPlaybackManager(playback, queueManager).MediaSessionCallback()

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        override fun onSeekTo(pos: Long) {
            playback.seekTo(pos)
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

        override fun onSetRepeatMode(repeatMode: Int) {

        }

        override fun onSetShuffleMode(shuffleMode: Int) {

        }

        override fun onPause() {
            //check the State i.e isPlaying ?
            playback.pause()

        }

        override fun onPlay() {

        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            val trackId = mediaId!!.toLong()
            queueManager.buildQueue(trackId, extras!!)
            onPlaybackStart()
            playback.playFromId(trackId)
        }
    }
}