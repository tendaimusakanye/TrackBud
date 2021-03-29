package com.tendai.common.playback

import android.net.Uri

interface Playback {
    fun playFromId(trackId: Long)
    fun pause()
    fun stop()
    fun getCurrentPosition()
    fun isPlaying(): Boolean
    fun reset()
    fun release()
    fun prepare()
    fun setSource(path: String): Boolean
    fun setSource(uri: Uri): Boolean
    fun seekTo(position: Long)
    fun getState(): Int
}

interface Callback {
    fun onCompletion()
}