package com.tendai.common.di

import android.content.Context
import com.tendai.common.MusicService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class, QueueModule::class, PlaybackModule::class, LocalDataSourceModule::class, ServiceModule::class])
interface ServiceComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ServiceComponent
    }

    fun inject(musicService: MusicService)
}

// TODO: 1/19/22 The @JVMStatic Annotation ?