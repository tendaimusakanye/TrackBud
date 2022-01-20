package com.tendai.common.playback.playback

import com.tendai.common.playback.Queue
import com.tendai.common.playback.di.TestRepositoryModule
import com.tendai.common.playback.di.TestServiceModule
import com.tendai.common.source.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DummyQueue @Inject constructor(
    @TestServiceModule.TestCoroutineScope testScope: CoroutineScope,
    @TestRepositoryModule.TestTracksRepository tracksRepository : Repository.Tracks
) : Queue(testScope, tracksRepository)