package com.tendai.common.playback.source.local

import android.content.Context
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.local.LocalTracksDataSource
import javax.inject.Inject

class DummyLocalTracksDataSource @Inject constructor(context: Context) :
    LocalDataSource.Tracks by LocalTracksDataSource(context)