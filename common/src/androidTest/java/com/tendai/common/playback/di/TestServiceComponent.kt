package com.tendai.common.playback.di

import android.content.Context
import com.tendai.common.di.*
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [RepositoryModule::class, QueueModule::class, PlaybackModule::class,
        LocalDataSourceModule::class, ServiceModule::class, TestServiceModule::class,
        TestLocalDataSourceModule::class, TestRepositoryModule::class]
)
interface TestServiceComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TestServiceComponent
    }
}