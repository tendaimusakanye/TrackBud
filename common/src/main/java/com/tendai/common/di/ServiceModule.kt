package com.tendai.common.di

import android.app.NotificationManager
import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.tendai.common.MediaNotificationManager
import com.tendai.common.MusicService
import com.tendai.common.playback.Playback
import com.tendai.common.playback.PlaybackManager
import com.tendai.common.playback.QueueManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
object ServiceModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class IoDispatcher

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MainDispatcher

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MediaSession

    @Singleton
    @MediaSession
    @Provides
    @JvmStatic // For performance
    fun provideMediaSession(context: Context): MediaSessionCompat = MediaSessionCompat(context, TAG)

    @Singleton
    @Provides
    @JvmStatic
    fun provideSessionToken(@MediaSession mediaSession: MediaSessionCompat): MediaSessionCompat.Token =
        mediaSession.sessionToken

    @Singleton
    @Provides
    @JvmStatic
    fun provideServiceScope(
        job: Job,
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(dispatcher + job)

    @Singleton
    @Provides
    @MainDispatcher
    @JvmStatic
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Singleton
    @Provides
    @JvmStatic
    fun provideSupervisorJob(): Job = SupervisorJob()

    @Singleton
    @Provides
    @IoDispatcher
    @JvmStatic
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    @JvmStatic
    fun providePlaybackManager(
        @MediaSession mediaSession: MediaSessionCompat,
        playback: Playback,
        queueManager: QueueManager
    ): PlaybackManager = PlaybackManager(mediaSession, playback, queueManager)

    @Singleton
    @Provides
    @JvmStatic
    fun provideMediaNotificationManager(
        controllerCompat: MediaControllerCompat,
        sessionToken: MediaSessionCompat.Token,
        context: Context,
        serviceScope: CoroutineScope,
        notificationManager: NotificationManager
    ): MediaNotificationManager =
        MediaNotificationManager(
            controllerCompat,
            sessionToken,
            context as MusicService,
            serviceScope,
            notificationManager
        )

    @Singleton
    @Provides
    @JvmStatic
    fun provideMediaControllerCompat(
        context: Context,
        sessionToken: MediaSessionCompat.Token
    ): MediaControllerCompat = MediaControllerCompat(context as MusicService, sessionToken)


    @Singleton
    @Provides
    @JvmStatic
    fun provideNotificationManager(context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

}

private const val TAG = "MusicService"