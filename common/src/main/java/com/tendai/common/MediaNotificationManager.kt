package com.tendai.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.RemoteException
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import androidx.palette.graphics.Palette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaNotificationManager(
    private val context: Context,
    private val service: MusicService,
    private val serviceScope: CoroutineScope,
    private val notificationManager: NotificationManager
) {
    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { newState ->
                playbackState = newState
                if (newState.state == PlaybackStateCompat.STATE_STOPPED
                    || newState.state == PlaybackStateCompat.STATE_NONE
                    || newState.state == PlaybackStateCompat.STATE_ERROR
                ) {
                    stopNotification()
                } else {
                    val notification = createNotification()
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            metadata?.let { newMetadata ->
                this@MediaNotificationManager.metadata = newMetadata
                val notification = createNotification()
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }

        override fun onSessionDestroyed() {
            try {
                updateSessionToken()
            } catch (e: RemoteException) {
                Log.e(TAG, "could not connect to media controller")
            }
        }
    }

    private var started = false
    private lateinit var sessionToken: MediaSessionCompat.Token
    private lateinit var metadata: MediaMetadataCompat
    private lateinit var controller: MediaControllerCompat
    private lateinit var playbackState: PlaybackStateCompat

    init {
        updateSessionToken()
    }

    fun startNotification() {
        if (!started) {
            metadata = controller.metadata
            playbackState = controller.playbackState

            val notification = createNotification()
            controller.registerCallback(controllerCallback)
            service.startForeground(NOTIFICATION_ID, notification)
            started = true
        }
    }

    fun stopNotification() {
        if (started) {
            started = false
            controller.unregisterCallback(controllerCallback)
            notificationManager.cancel(NOTIFICATION_ID)
            service.stopForeground(true)
        }
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val style = MediaStyle()
            .setMediaSession(sessionToken)
            .setShowCancelButton(true)
            .setShowActionsInCompactView(1)
            .setCancelButtonIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

        val playButtonResId = if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }

        val description = metadata.description
        val builder = NotificationCompat.Builder(context, MUSIC_X_CHANNEL_ID).apply {
            setStyle(style)
            setContentIntent(controller.sessionActivity)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setLargeIcon(description.iconBitmap)
            addAction(setPreviousAction())
            addAction(setPlayPauseAction(playbackState.state, playButtonResId))
            addAction(setNextAction())
            color = getColorFromArt()
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
        }
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(MUSIC_X_CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                MUSIC_X_CHANNEL_ID,
                context.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.description =
                context.getString(R.string.notification_channel_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private fun getColorFromArt(): Int {
        var color = -1
        serviceScope.launch {
            withContext(IO) {
                metadata.description?.iconBitmap?.let {
                    color = Palette.from(it)
                        .generate()
                        .getVibrantColor(Color.parseColor("#403f4d"))
                }
            }
        }
        return color
    }

    private fun setPreviousAction(): NotificationCompat.Action {
        val actionIntent = Intent(context, MusicService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
        return NotificationCompat.Action(
            R.drawable.ic_previous,
            context.getString(R.string.title_previous),
            pendingIntent
        )
    }

    private fun setNextAction(): NotificationCompat.Action {
        val actionIntent = Intent(context, MusicService::class.java).apply {
            action = ACTION_NEXT
        }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
        return NotificationCompat.Action(
            R.drawable.ic_next,
            context.getString(R.string.title_next),
            pendingIntent
        )
    }

    private fun setPlayPauseAction(
        state: Int,
        @IdRes playPauseResId: Int
    ): NotificationCompat.Action {
        val actionIntent = Intent(context, MusicService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
        val label =
            if (state == PlaybackStateCompat.STATE_PLAYING) context.getString(R.string.title_play)
            else context.getString(R.string.title_pause)
        return NotificationCompat.Action(playPauseResId, label, pendingIntent)
    }


    @Throws(RemoteException::class)
    private fun updateSessionToken() {
        val freshToken = service.sessionToken
        if (sessionToken != freshToken
        ) {
            controller.unregisterCallback(controllerCallback)
            freshToken?.let { sessionToken = it }
            controller = MediaControllerCompat(service, sessionToken)
            if (started) {
                controller.registerCallback(controllerCallback)
            }
        }
    }
}

const val NOTIFICATION_ID = 98716
const val MUSIC_X_CHANNEL_ID = "com.tendai.common.MusicXChannelId"

//todo: add the proper contentIntent when the UI is done.
//todo: setOngoing is it necessary ?