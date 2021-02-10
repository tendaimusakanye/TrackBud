package com.tendai.common.media

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
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
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        Service.START_STICKY

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
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
                    //TODO("Handle saving the most recent song")
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
                            metadatas = trackRepository.getTracksByArtist(
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
            val mediaItems = metadatas.map {
                MediaItem(it.description, it.flag)
            }
            result.sendResult(mediaItems.toMutableList())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //release the media session when the music service is destroyed
        mediaSession.run {
            isActive = false
            release()
        }
        //cancels the coroutines when going away. I guess it it to avoid memory leaks.
        serviceJob.cancel()
    }
}

const val EXTRA_ALBUM_ID = "com.tendai.common.media.EXTRA_ALBUM_ID"
const val EXTRA_PLAYLIST_ID = "com.tendai.common.media.EXTRA_PLAYLIST_ID"
const val EXTRA_ARTIST_ID = "com.tendai.common.media.EXTRA_ARTIST_ID"
const val IS_ALL_ARTISTS = "com.tendai.common.media.IS_ALL_ARTISTS"
const val IS_ARTIST_TRACKS = "com.tendai.common.media.IS_ARTIST_TRACKS"
const val IS_ARTIST_ALBUMS = "com.tendai.common.media.IS_ARTIST_ALBUMS"
const val IS_ALBUM = "com.tendai.common.media.IS_ALBUM"
const val IS_PLAYLIST = "com.tendai.common.media.IS_PLAYLIST"
const val DISCOVER_ROOT = "DISCOVER"
const val TRACKS_ROOT = "TRACKS"
const val RECENT_ROOT = "RECENT_SONG"
const val ARTISTS_ROOT = "ARTISTS"
const val TAG: String = "MusicService "

