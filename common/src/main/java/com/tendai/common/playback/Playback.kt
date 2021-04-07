package com.tendai.common.playback

interface Playback {
    fun playFromId(trackId: Long)
    fun pause()
    fun stop()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun seekTo(position: Int)
    fun getState(): Int
    fun release()
    fun requestFocus(): Int
}

interface Callback {
    fun onCompletion()

    fun onPrepared()
}