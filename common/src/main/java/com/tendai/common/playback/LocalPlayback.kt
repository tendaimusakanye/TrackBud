package com.tendai.common.playback

import android.content.ContentUris
import android.content.Context
import android.net.Uri

class LocalPlayback(private val context: Context) : Playback {
    private lateinit var onCompletion: Callback
//    private val mediaPlayer
//        get() {
//            MediaPlayer().apply {
//
//            }
//                .prepare()
//
//        }

    override fun playFromId(trackId: Long) {
        val uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
    }

    override fun pause() {

    }

    override fun stop() {

    }

    override fun getCurrentPosition() {

    }

    override fun isPlaying() : Boolean{
        TODO("Not yet implemented")
    }

    override fun reset() {

    }

    override fun release() {

    }

    override fun prepare() {

    }

    override fun setSource(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun setSource(uri: Uri): Boolean {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Long) {

    }

    override fun getState():Int {
        TODO("Not yet implemented")
    }

}

