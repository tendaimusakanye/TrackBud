package com.tendai.common.data.source.file

interface FileDataSource {
    //todo: return model which are not yet implemented.
    suspend fun getTracksFromExternalStorage()

    suspend fun getTrackFromPath(path: String)
}