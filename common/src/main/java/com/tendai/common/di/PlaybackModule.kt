package com.tendai.common.di

import com.tendai.common.playback.LocalPlayback
import com.tendai.common.playback.Playback
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class PlaybackModule {

    @Singleton
    @Binds
    abstract fun bindPlayback(localPlayback: LocalPlayback): Playback
}