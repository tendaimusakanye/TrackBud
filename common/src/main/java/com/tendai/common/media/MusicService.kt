package com.tendai.common.media

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.tendai.common.media.extensions.flag
import com.tendai.common.media.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class MusicService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaNotificationManager: MediaNotificationManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var trackRepository: Repository.Tracks
    private lateinit var albumRepository: Repository.Albums
    private lateinit var playlistRepository: Repository.Playlists
    private lateinit var artistRepository: Repository.Artists

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
            setCallback(object : MediaSessionCompat.Callback() {
                //todo: Implement my own media session callback
            })
            //todo: I also can set this in the onPlay method of My media session callback.
            isActive = true
        }

        // Setting  the media session token
        sessionToken = mediaSession.sessionToken

        //initializing the notification manager
        //todo: initialize my notification manager
        //todo: handle listening to external storage i.e. memory cards or waiting for permission read storage.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? = BrowserRoot(TRACKS_ROOT, null)


    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaItem>>,
        options: Bundle
    ) {
        serviceScope.launch {
            when (parentId) {
                TRACKS_ROOT -> {
                    if (!options.getBoolean(EXTRA_TRACK_ID)) {
                        val children = trackRepository.getTracks().map {
                            MediaItem(it.description, it.flag)
                        }
                        result.sendResult(children.toMutableList())
                    }
                }
                DISCOVER_ROOT -> {
                    if (!options.getBoolean(EXTRA_PLAYLIST_ID) &&
                        !options.getBoolean(EXTRA_ALBUM_ID)
                    ) {
                        val children = mutableListOf<MediaItem>()
                        val albums = albumRepository.getAlbums(5).map {
                            MediaItem(it.description, it.flag)
                        }
                        val playlists = playlistRepository.getAllPlaylists(5).map {
                            MediaItem(it.description, it.flag)
                        }
                        children += albums
                        children += playlists
                        result.sendResult(children)
                    }
                }
                ARTISTS_ROOT ->{
                    if
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //release the media session when the music service is destroyed
        mediaSession.run {
            isActive = false
            release()
        }
    }
}

const val EXTRA_ALBUM_ID = "com.tendai.common.media.EXTRA_ALBUM_ID"
const val EXTRA_PLAYLIST_ID = "com.tendai.common.media.EXTRA_PLAYLIST_ID"
const val EXTRA_TRACK_ID = "com.tendai.common.media.EXTRA_TRACK_ID"
const val DISCOVER_ROOT = "DISCOVER"
const val TRACKS_ROOT = "TRACKS"
const val ARTISTS_ROOT = "ARTISTS"
const val TAG: String = "MusicService "