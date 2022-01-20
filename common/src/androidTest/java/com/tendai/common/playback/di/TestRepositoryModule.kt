package com.tendai.common.playback.di

import com.tendai.common.playback.source.DummyTracksRepository
import com.tendai.common.source.Repository
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Qualifier
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
abstract class TestRepositoryModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestTracksRepository

    @Binds
    @Singleton
    @TestTracksRepository
    abstract fun bindTracksRepository(tracksRepository: DummyTracksRepository): Repository.Tracks
}