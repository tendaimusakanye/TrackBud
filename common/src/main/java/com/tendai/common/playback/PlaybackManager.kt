package com.tendai.common.playback

import android.support.v4.media.session.PlaybackStateCompat

typealias PlaybackStart = () -> Unit
typealias PlaybackStop = () -> Unit
typealias PlaybackStateChanged = (newState: PlaybackStateCompat) -> Unit
typealias ShowNotification = () -> Unit


abstract class PlaybackManager(
    val playback: Playback,
    val queueManager: QueueManager
) {
    lateinit var onPlaybackStart: PlaybackStart
    private lateinit var onPlaybackStop: PlaybackStop
    private lateinit var onPlaybackStateChanged: PlaybackStateChanged
    private lateinit var onNotificationRequired: ShowNotification

    private fun updatePlaybackState() {

    }

    private fun getAvailableActions() {

    }

    fun onPlaybackStart(onPlaybackStart: PlaybackStart) {
        this.onPlaybackStart = onPlaybackStart
    }

    fun onPlaybackStateChanged(onPlaybackStateChanged: PlaybackStateChanged) {
        this.onPlaybackStateChanged = onPlaybackStateChanged
    }

    fun onPlaybackStopped(onPlaybackStopped: PlaybackStop) {
        this.onPlaybackStop = onPlaybackStopped
    }

    fun onNotificationRequired(onNotificationRequired: ShowNotification) {
        this.onNotificationRequired = onNotificationRequired
    }


}

