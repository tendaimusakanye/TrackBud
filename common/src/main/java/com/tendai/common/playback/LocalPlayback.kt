package com.tendai.common.playback

import android.content.*
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.IntDef
import javax.inject.Inject

@IntDef(
    STATE_PLAYING,
    STATE_PAUSED,
    STATE_STOPPED,
    STATE_IDLE,
    STATE_NONE,
    STATE_INITIALIZED,
    STATE_ERROR
)
@Retention(AnnotationRetention.RUNTIME)

annotation class State

class LocalPlayback @Inject constructor(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) : Playback,
    AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener {

    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val becomingNoisy = BecomingNoisyReceiver()
    private val controller = MediaControllerCompat(context, sessionToken)
    private var _mediaPlayer: MediaPlayer? = null
    private val mediaPlayer: MediaPlayer
        get() {
            if (_mediaPlayer == null) {
                _mediaPlayer = createMediaPlayer(context)
                if (_mediaPlayer != null) state = STATE_IDLE
            }
            return _mediaPlayer
                ?: throw IllegalStateException("Failed to create MediaPlayer Instance")
        }

    @State
    private var state = STATE_NONE
    private var isRegistered = false
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private lateinit var callback: Callback
    private lateinit var focusRequest: AudioFocusRequest

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(this@LocalPlayback, Handler(Looper.getMainLooper()))
                build()
            }
        }
    }

    override fun playFromId(trackId: Long) {
        val uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
        if (state == STATE_PAUSED) {
            onPrepared(mediaPlayer)
            return
        }

        mediaPlayer.reset()
        state = STATE_IDLE
        state = try {
            mediaPlayer.setDataSource(context, uri)
            STATE_INITIALIZED
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Setting source failed")
            STATE_IDLE
        }
        if (state == STATE_INITIALIZED) {
            mediaPlayer.prepareAsync()
        }
    }

    override fun pause() {
        mediaPlayer.pause()
        state = STATE_PAUSED
        unregisterNoisy()
    }

    override fun stop() {
        mediaPlayer.stop()
        state = STATE_STOPPED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest)
        }
        unregisterNoisy()
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun isPlaying(): Boolean {
        if (mediaPlayer.isPlaying) state = STATE_PLAYING
        return mediaPlayer.isPlaying
    }

    override fun seekTo(position: Int) = mediaPlayer.seekTo(position)

    override fun getState(): Int {
        return when (state) {
            STATE_STOPPED -> PlaybackStateCompat.STATE_STOPPED
            STATE_IDLE -> PlaybackStateCompat.STATE_NONE
            STATE_ERROR -> PlaybackStateCompat.STATE_ERROR
            STATE_INITIALIZED -> PlaybackStateCompat.STATE_NONE
            STATE_PAUSED -> PlaybackStateCompat.STATE_PAUSED
            STATE_PLAYING -> PlaybackStateCompat.STATE_PLAYING
            else -> PlaybackStateCompat.STATE_NONE
        }
    }

    override fun release() {
        mediaPlayer.release()
        _mediaPlayer = null
        unregisterNoisy()
        state = STATE_NONE
    }

    @Suppress("DEPRECATION")
    override fun requestFocus(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(focusRequest)
        } else {
            audioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    // from mediaPlayer
    override fun onCompletion(mp: MediaPlayer?) {
        state = STATE_IDLE
        callback.onCompletion()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        state = STATE_ERROR
        Log.e(TAG, " Some weird stuff with MediaPlayer \nWhat: $what\nExtras: $extra")
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer.start()
        registerNoisy()
        state = STATE_PLAYING
        callback.onPrepared()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                controller.transportControls.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                controller.transportControls.pause()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                controller.transportControls.play()
            }
        }
    }

    private fun registerNoisy() {
        if (!isRegistered) {
            context.registerReceiver(becomingNoisy, noisyIntentFilter)
            isRegistered = true
        }
    }

    private fun unregisterNoisy() {
        if (isRegistered) {
            context.unregisterReceiver(becomingNoisy)
            isRegistered = false
        }
    }

    private fun createMediaPlayer(context: Context): MediaPlayer {
        return MediaPlayer().apply {
            setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }
            setOnPreparedListener(this@LocalPlayback)
            setOnCompletionListener(this@LocalPlayback)
            setOnErrorListener(this@LocalPlayback)
        }
    }

    private inner class BecomingNoisyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                if (state == STATE_PLAYING) {
                    controller.transportControls.pause()
                }
            }
        }
    }
}

const val STATE_PLAYING = 1
const val STATE_PAUSED = 2
const val STATE_STOPPED = 3
const val STATE_INITIALIZED = 4
const val STATE_IDLE = 5
const val STATE_NONE = 7
const val STATE_ERROR = 6
private const val TAG = "LocalPlayback"

//todo:  enums or annotations which and why ?
//todo: nice to haves -> add ducking on audioFocus Loss
