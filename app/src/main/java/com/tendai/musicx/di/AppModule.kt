package com.tendai.musicx.di

import android.content.ComponentName
import android.content.Context
import com.tendai.common.ClientServiceConnection
import com.tendai.common.MusicService
import dagger.Module
import dagger.Provides

@Module
object AppModule {

    @Provides
    @AppScope
    fun provideClientServiceConnection(
        context: Context,
        component: ComponentName
    ): ClientServiceConnection = ClientServiceConnection(context, component)

    @Provides
    @AppScope
    fun provideServiceComponent(context: Context): ComponentName =
        ComponentName(context, MusicService::class.java)
}