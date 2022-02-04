package com.tendai.common

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat

/**
 * Class that manages a connection to a [MediaBrowserServiceCompat] instance, typically a
 * [MusicService] or one of its subclasses.
 *
 * Typically it's best to construct/inject dependencies either using DI or, as UAMP does,
 * using [InjectorUtils] in the app module. There are a few difficulties for that here:
 * - [MediaBrowserCompat] is a final class, so mocking it directly is difficult.
 * - A [MediaBrowserConnectionCallback] is a parameter into the construction of
 *   a [MediaBrowserCompat], and provides callbacks to this class.
 * - [MediaBrowserCompat.ConnectionCallback.onConnected] is the best place to construct
 *   a [MediaControllerCompat] that will be used to control the [MediaSessionCompat].
 *
 *  Because of these reasons, rather than constructing additional classes, this is treated as
 *  a black box (which is why there's very little logic here).
 *
 *  This is also why the parameters to construct a [ServiceConnection] are simple
 *  parameters, rather than private properties. They're only required to build the
 *  [MediaBrowserConnectionCallback] and [MediaBrowserCompat] objects.
 */
class ServiceConnection(context: Context, serviceComponent: ComponentName) {
    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }

    val rootMediaId: String get() = mediaBrowser.root

    val playbackState = MutableLiveData<PlaybackStateCompat>()
        .apply { postValue(EMPTY_PLAYBACK_STATE) }

    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
        .apply { postValue(NOTHING_PLAYING) }

    val queueItems = MutableLiveData<List<MediaSessionCompat.QueueItem>>()
        .apply { postValue(EMPTY_QUEUE) }

    val queueTitle = MutableLiveData<CharSequence>()
        .apply { postValue(EMPTY_QUEUE_TITLE) }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    lateinit var mediaController: MediaControllerCompat
        private set

    fun subscribe(
        parentId: String,
        options: Bundle,
        callback: MediaBrowserCompat.SubscriptionCallback
    ) = mediaBrowser.subscribe(parentId, options, callback)

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) =
        mediaBrowser.unsubscribe(parentId, callback)

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            queueItems.postValue(queue ?: EMPTY_QUEUE)
        }

        override fun onQueueTitleChanged(title: CharSequence?) {
            queueTitle.postValue(title ?: EMPTY_QUEUE_TITLE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            nowPlaying.postValue(metadata ?: NOTHING_PLAYING)
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {

        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        override fun onConnected() {
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            isConnected.postValue(true)
        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }
}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, null)
    .build()

val EMPTY_QUEUE = emptyList<MediaSessionCompat.QueueItem>()
const val EMPTY_QUEUE_TITLE = "EMPTY_QUEUE_TITLE"
