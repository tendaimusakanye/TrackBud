package com.tendai.common.playback.di

import com.tendai.common.playback.MainCoroutineRule
import com.tendai.common.playback.playback.DummyQueue
import com.tendai.common.source.Repository
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import javax.inject.Qualifier
import javax.inject.Singleton

@ExperimentalCoroutinesApi
object TestServiceModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestDispatcher

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestCoroutineScope

    @Provides
    @JvmStatic
    @TestDispatcher
    @Singleton
    fun provideTestDispatcher(): CoroutineDispatcher = TestCoroutineDispatcher()

    @Provides
    @JvmStatic
    @Singleton
    fun provideQueue(
        @TestCoroutineScope testScope: CoroutineScope,
        @TestRepositoryModule.TestTracksRepository tracksRepository: Repository.Tracks
    ): DummyQueue = DummyQueue(testScope, tracksRepository)


    @Provides
    @JvmStatic
    @Singleton
    @TestCoroutineScope
    fun provideTestCoroutineScope(): CoroutineScope = MainCoroutineRule()

    @Provides
    @Singleton
    fun provideCoroutineRule(): MainCoroutineRule = MainCoroutineRule()
}