package com.tendai.common.playback

import android.content.ContentUris
import android.content.Context

class LocalPlayer(private val context: Context) : Playback{

//    private val mediaPlayer
//        get() {
//            MediaPlayer().apply {
//
//            }
//                .prepare()
//
//        }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun playFromId(trackId: Long) {
        val uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun next() {
        TODO("Not yet implemented")
    }

    override fun previous() {
        TODO("Not yet implemented")
    }

    override fun repeat() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun shuffle() {
        TODO("Not yet implemented")
    }

    override fun getPlaybackSate(): Int {
        TODO("Not yet implemented")
    }

    override fun setPlaybackState() {
        TODO("Not yet implemented")
    }

    override fun seekTo(pos: Long) {
        TODO("Not yet implemented")
    }

}

