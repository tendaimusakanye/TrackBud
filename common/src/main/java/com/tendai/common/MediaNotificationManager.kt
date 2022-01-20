package com.tendai.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
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
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaNotificationManager @Inject constructor(
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
                    serviceScope.launch {
                        val notification = createNotification()
                        notificationManager.notify(NOTIFICATION_ID, notification)
                    }

                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            metadata?.let { newMetadata ->
                this@MediaNotificationManager.metadata = newMetadata
                serviceScope.launch {
                    val notification = createNotification()
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
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
        try {
            updateSessionToken()
        } catch (e: RemoteException) {
            Log.e(TAG, "Could not create Media Controller")
        }
    }

    fun startNotification() {
        if (!started) {
            metadata = controller.metadata
            playbackState = controller.playbackState

            serviceScope.launch {
                val notification = createNotification()
                controller.registerCallback(controllerCallback)
                service.startForeground(NOTIFICATION_ID, notification)
            }
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

    private suspend fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val style = MediaStyle()
            .setMediaSession(sessionToken)
            .setShowCancelButton(true)
            .setShowActionsInCompactView(1)
            .setCancelButtonIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    service,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

        val playButtonResId = if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }

        val description = metadata.description
        val builder = NotificationCompat.Builder(service, MUSIC_X_CHANNEL_ID).apply {
            setStyle(style)
            setContentIntent(controller.sessionActivity)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setLargeIcon(createBitmap(description.iconUri))
            addAction(setPreviousAction())
            addAction(setPlayPauseAction(playbackState.state, playButtonResId))
            addAction(setNextAction())
            color = getColorFromArt() ?: Color.parseColor("#cfd8dc")
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    service,
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
                service.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description =
                service.getString(R.string.notification_channel_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createBitmap(uri: Uri?): Bitmap? {
        val placeHolderBitmap = BitmapFactory.decodeResource(service.resources, R.drawable.ic_placeholder_art)

        return uri?.let {
            var inputStream: InputStream? = null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(service.contentResolver, it)
                    return ImageDecoder.decodeBitmap(source)
                }
                inputStream = service.contentResolver.openInputStream(it)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
                placeHolderBitmap
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                placeHolderBitmap
            } finally {
                inputStream?.close()
            } ?: placeHolderBitmap
        }
    }


    private suspend fun getColorFromArt(): Int? = withContext(IO) {
        metadata.description?.iconBitmap?.let { bitmap ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val width = bitmap.width / 2
                val height = bitmap.height / 2

                return@withContext Palette.from(bitmap)
                    .generate()
                    .getVibrantColor(bitmap.getColor(width, height).toArgb())
            } else {
                return@withContext Palette.from(bitmap)
                    .generate()
                    .getVibrantColor(Color.parseColor("#880e4f"))
            }
        }
    }

    private fun setPreviousAction(): NotificationCompat.Action {
        val actionIntent = Intent(service, MusicService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val pendingIntent = PendingIntent.getService(service, 0, actionIntent, 0)
        return NotificationCompat.Action(
            R.drawable.ic_previous,
            service.getString(R.string.title_previous),
            pendingIntent
        )
    }

    private fun setNextAction(): NotificationCompat.Action {
        val actionIntent = Intent(service, MusicService::class.java).apply {
            action = ACTION_NEXT
        }
        val pendingIntent = PendingIntent.getService(service, 0, actionIntent, 0)
        return NotificationCompat.Action(
            R.drawable.ic_next,
            service.getString(R.string.title_next),
            pendingIntent
        )
    }

    private fun setPlayPauseAction(
        state: Int,
        @IdRes playPauseResId: Int
    ): NotificationCompat.Action {
        val actionIntent = Intent(service, MusicService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val pendingIntent = PendingIntent.getService(service, 0, actionIntent, 0)
        val label =
            if (state == PlaybackStateCompat.STATE_PLAYING) service.getString(R.string.title_play)
            else service.getString(R.string.title_pause)
        return NotificationCompat.Action(playPauseResId, label, pendingIntent)
    }


    @Throws(RemoteException::class)
    private fun updateSessionToken() {
        val freshToken = service.sessionToken
        if (sessionToken != freshToken
        ) {
            controller.unregisterCallback(controllerCallback)
            freshToken?.let {
                sessionToken = it
                controller = MediaControllerCompat(service, sessionToken)
            }
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