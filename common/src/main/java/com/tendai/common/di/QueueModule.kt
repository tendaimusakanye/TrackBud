package com.tendai.common.di

import com.tendai.common.playback.Queue
import com.tendai.common.playback.QueueManager
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class QueueModule {

    @Singleton
    @Binds
    abstract fun bindQueue(queue: Queue): QueueManager
}