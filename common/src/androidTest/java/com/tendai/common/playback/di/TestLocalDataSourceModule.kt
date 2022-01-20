package com.tendai.common.playback.di

import com.tendai.common.playback.source.local.DummyLocalTracksDataSource
import com.tendai.common.source.local.LocalDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
abstract class TestLocalDataSourceModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestLocalTracksDataSource

    @Provides
    @Singleton
    @TestLocalTracksDataSource
    abstract fun bindTracksDataSource(tracksDataSource: DummyLocalTracksDataSource): LocalDataSource.Tracks
}