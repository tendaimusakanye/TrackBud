package com.tendai.common

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.tendai.common.extensions.flag
import com.tendai.common.playback.PlaybackManager
import com.tendai.common.playback.QueueManager
import com.tendai.common.playback.REPEAT_MODE
import com.tendai.common.playback.SHUFFLE_MODE
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class MusicService : MediaBrowserServiceCompat() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaNotificationManager: MediaNotificationManager
    private lateinit var playbackManager: PlaybackManager
    private lateinit var queueManager: QueueManager

    private lateinit var trackRepository: Repository.Tracks
    private lateinit var albumRepository: Repository.Albums
    private lateinit var playlistRepository: Repository.Playlists
    private lateinit var artistRepository: Repository.Artists

    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()

        //Pending intent to launch the Ui of the Music Player from the notification Panel
        val sessionPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        // initializing media session
        mediaSession = MediaSessionCompat(this, TAG).apply {
            setSessionActivity(sessionPendingIntent)
            setCallback(playbackManager.mediaSessionCallback)
        }
        // Setting  the media session token
        sessionToken = mediaSession.sessionToken
        playbackManager.updatePlaybackState()

        //todo: lambdas vs callbacks ?
        //todo: can DI initialize my notification manager in OnCreate or it's done physically
        //todo: request Storage permissions.
        setUpMetadataListeners()
        setUpPlaybackListeners()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                when (mediaSession.controller.playbackState.state) {
                    PlaybackStateCompat.STATE_PLAYING -> mediaSession.controller.transportControls.pause()
                    PlaybackStateCompat.STATE_PAUSED -> mediaSession.controller.transportControls.play()
                }
            }
            ACTION_NEXT -> mediaSession.controller.transportControls.skipToNext()
            ACTION_PREVIOUS -> mediaSession.controller.transportControls.skipToPrevious()
            else -> MediaButtonReceiver.handleIntent(mediaSession, intent)
        }
        return Service.START_STICKY
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        val isRecentRequest =
            rootHints?.getBoolean(BrowserRoot.EXTRA_RECENT) ?: false

        var extras: Bundle? = null
        val rootId = if (isRecentRequest) {
            extras = Bundle()
            extras.putBoolean(BrowserRoot.EXTRA_RECENT, true)
            RECENT_ROOT
        } else TRACKS_ROOT

        return BrowserRoot(rootId, extras)
    }

    // The logic in this function is based on the UI Design of my application.
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>,
        options: Bundle
    ) {
        serviceScope.launch {
            var metadatas = listOf<MediaMetadataCompat>()
            when (parentId) {
                RECENT_ROOT -> {
                    //TODO("Handle saving the most recent song & building a proper queue for the item
                    // allowed browsing types in onGet Root")
                }
                TRACKS_ROOT -> {
                    metadatas = trackRepository.getTracks()
                }
                DISCOVER_ROOT -> {
                    when {
                        (!options.getBoolean(IS_ALBUM) && !options.getBoolean(IS_PLAYLIST)) -> {
                            val children = mutableListOf<MediaMetadataCompat>()
                            val albums = albumRepository.getAlbums(5)
                            val playlists = playlistRepository.getAllPlaylists(5)

                            children += albums
                            children += playlists
                            metadatas = children
                        }
                        options.getBoolean(IS_ALBUM) -> {
                            metadatas = trackRepository.getTracksInAlbum(
                                options.getLong(EXTRA_ALBUM_ID)
                            )
                        }
                        options.getBoolean(IS_PLAYLIST) -> {
                            metadatas = trackRepository.getTracksInPlaylist(
                                options.getLong(EXTRA_PLAYLIST_ID)
                            )
                        }
                    }
                }
                ARTISTS_ROOT -> {
                    when {
                        options.getBoolean(IS_ALL_ARTISTS) -> {
                            metadatas = artistRepository.getAllArtists()
                        }
                        options.getBoolean(IS_ARTIST_TRACKS) -> {
                            metadatas = trackRepository.getTracksForArtist(
                                options.getLong(EXTRA_ARTIST_ID)
                            )
                        }
                        options.getBoolean(IS_ARTIST_ALBUMS) -> {
                            metadatas = albumRepository.getAlbumsByArtist(
                                options.getLong(EXTRA_ARTIST_ID)
                            )
                        }
                    }
                }
            }
            if (options.getBoolean(BrowserRoot.EXTRA_RECENT)) {
                //TODO("")
            } else {
                val mediaItems = metadatas.map {
                    MediaItem(it.description, it.flag)
                }
                result.sendResult(mediaItems.toMutableList())
            }
        }
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }
        //todo: saveRecentTrack
        playbackManager.cleanUp()
        mediaNotificationManager.stopNotification()
        //cancels the coroutines when going away. I guess it iS to avoid memory leaks.
        serviceJob.cancel()
        super.onDestroy()
    }

    private fun setUpPlaybackListeners() {
        playbackManager.onNotificationRequiredListener { state ->
            if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
                mediaNotificationManager.startNotification()
            }
        }

        playbackManager.onPlaybackStarted {
            startService(Intent(applicationContext, MusicService::class.java))
            if (!mediaSession.isActive) mediaSession.isActive = true
        }

        playbackManager.onPlaybackPaused {
            stopForeground(false)
        }

        playbackManager.onPlaybackStateUpdated {
            mediaSession.setPlaybackState(it)
            it.extras?.let { bundle ->
                mediaSession.setRepeatMode(bundle.getInt(REPEAT_MODE))
                mediaSession.setShuffleMode(bundle.getInt(SHUFFLE_MODE))
            }
        }

        playbackManager.onPlaybackStopped {
            stopSelf()
            mediaSession.isActive = false
            stopForeground(true)
            // todo: save current playing song to storage
        }
    }

    private fun setUpMetadataListeners() {
        //listeners
        queueManager.onMetadataChangedListener { metadata ->
            mediaSession.setMetadata(metadata)
        }

        queueManager.onQueueChangedListener { title, queueItems ->
            mediaSession.setQueueTitle(title)
            mediaSession.setQueue(queueItems)
        }
    }
}

const val EXTRA_ALBUM_ID = "com.tendai.common.EXTRA_ALBUM_ID"
const val EXTRA_PLAYLIST_ID = "com.tendai.common.EXTRA_PLAYLIST_ID"
const val EXTRA_ARTIST_ID = "com.tendai.common.EXTRA_ARTIST_ID"

const val IS_ALL_ARTISTS = "com.tendai.common.IS_ALL_ARTISTS"
const val IS_ARTIST_TRACKS = "com.tendai.common.IS_ARTIST_TRACKS"
const val IS_ARTIST_ALBUMS = "com.tendai.common.IS_ARTIST_ALBUMS"
const val IS_ALBUM = "com.tendai.common.IS_ALBUM"
const val IS_PLAYLIST = "com.tendai.common.IS_PLAYLIST"

const val DISCOVER_ROOT = "DISCOVER"
const val TRACKS_ROOT = "TRACKS"
const val RECENT_ROOT = "RECENT_SONG"
const val ARTISTS_ROOT = "ARTISTS"

const val ACTION_PLAY_PAUSE = "com.tendai.common.ACTION_PLAY_PAUSE"
const val ACTION_NEXT = "com.tendai.common.ACTION_NEXT"
const val ACTION_PREVIOUS = "com.tendai.common.ACTION_PREVIOUS"
const val TAG: String = "MusicService "

//TODO("Handle an empty root and add the systemUi logic for android 11")
//todo: implement DI with Dagger.
//TODO("Add a browsable root for android wear. I think it does have a viewpager. On Second thought
// I think it does.")