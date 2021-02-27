package com.tendai.common

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.tendai.common.extensions.flag
import com.tendai.common.playback.PlaybackManager
import com.tendai.common.playback.QueueManager
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class MusicService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaNotificationManager: MediaNotificationManager
    private lateinit var playbackManager: PlaybackManager
    private lateinit var queueManager: QueueManager

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
        }

        queueManager.onMetadataChanged { metadata ->
            mediaSession.setMetadata(metadata)
        }
        playbackManager.onPlaybackStart {
            mediaSession.isActive = true
        }


        // Setting  the media session token
        sessionToken = mediaSession.sessionToken

        //initializing the notification manager
        //todo: initialize my notification manager
        //todo: handle listening to external storage i.e. memory cards or waiting for permission read storage.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        Service.START_STICKY

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // see @link [https://developer.android.com/guide/topics/media/media-controls]
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
                                options.getInt(EXTRA_ALBUM_ID)
                            )
                        }
                        options.getBoolean(IS_PLAYLIST) -> {
                            metadatas = trackRepository.getTracksInPlaylist(
                                options.getInt(EXTRA_PLAYLIST_ID)
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
                                options.getInt(EXTRA_ARTIST_ID)
                            )
                        }
                        options.getBoolean(IS_ARTIST_ALBUMS) -> {
                            metadatas = albumRepository.getAlbumsByArtist(
                                options.getInt(EXTRA_ARTIST_ID)
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
        super.onDestroy()
        //release the media session when the music service is destroyed
        mediaSession.run {
            isActive = false
            release()
        }
        //cancels the coroutines when going away. I guess it iS to avoid memory leaks.
        serviceJob.cancel()
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

const val TAG: String = "MusicService "

//TODO("Handle an empty root and add the systemUi logic for android 11")
//TODO("Add a browsable root for android wear. I think it does have a viewpager. On Second thought
// I think it does.")