package com.tendai.common.playback

interface Playback {
    fun playFromId(trackId: Long)
    fun pause()
    fun stop()
    fun repeat()
    fun shuffle()
    fun getPlaybackSate(): Int
    fun setPlaybackState()
    fun seekTo(position: Long)
}