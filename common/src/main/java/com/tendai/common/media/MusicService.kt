package com.tendai.common.media

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat


class MusicService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaNotificationManager: MediaNotificationManager

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

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

        TODO("Retrieve songs from localStorage")
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Browsable etc etc. to build a representation of the UI etc etc.")
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

const val TAG: String = "MusicService "