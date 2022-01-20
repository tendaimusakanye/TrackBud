package com.tendai.common.playback.source

import com.tendai.common.playback.di.TestLocalDataSourceModule
import com.tendai.common.playback.di.TestServiceModule
import com.tendai.common.source.Repository
import com.tendai.common.source.TracksRepository
import com.tendai.common.source.local.LocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DummyTracksRepository @Inject constructor(
    @TestLocalDataSourceModule.TestLocalTracksDataSource tracksDataSource: LocalDataSource.Tracks,
    @TestServiceModule.TestDispatcher dispatcher: CoroutineDispatcher
) : Repository.Tracks by TracksRepository(tracksDataSource, dispatcher)