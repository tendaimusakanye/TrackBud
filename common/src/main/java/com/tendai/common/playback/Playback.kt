package com.tendai.common.playback

interface Playback {
    fun play()
    fun playFromId(trackId: Long)
    fun pause()
    fun next()
    fun stop()
    fun previous()
    fun repeat()
    fun shuffle()
    fun getPlaybackSate(): Int
    fun setPlaybackState()
    fun seekTo(pos: Long)
}