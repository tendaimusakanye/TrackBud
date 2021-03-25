package com.tendai.common.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat

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
        when(action){

        }
    }

    override fun onSkipToPrevious() {
        playbackManager.handlePreviousOrNextRequest(-1)

    }

    override fun onStop() {
        playback.stop()
        playbackManager.onPlaybackStop()
        playbackManager.updatePlaybackState()
    }

    override fun onSkipToNext() {
        playbackManager.handlePreviousOrNextRequest(1)
    }

    override fun onPause() {
        playbackManager.updatePlaybackState()
        playback.pause()
        playbackManager.onPlaybackPause()
    }

    //todo:  see if invoking super here has any effect or not ?
    override fun onSetShuffleMode(shuffleMode: Int) {

    }

    override fun onSetRepeatMode(repeatMode: Int) {

    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        val trackId = mediaId!!.toLong()
        queueManager.buildQueue(trackId, extras!!)
        playbackManager.handlePlayRequest(trackId)
    }
}