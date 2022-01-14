package com.tendai.common.source

import android.graphics.Bitmap
import com.tendai.common.source.local.LocalDataSource
import com.tendai.common.source.model.Track

 class FakeTracksDataSource : LocalDataSource.Tracks {
    override suspend fun getTrackDetails(trackId: Long): Track {
        TODO("Not yet implemented")
    }

    override suspend fun getTracks(): List<Track> {
        val tracks = mutableListOf<Track>()
        //why does starting at 1 makes the tests fail ?
        for (i in 0..31) {
            tracks.add(
                i,
                Track(
                    i.toLong(),
                    "Track $i",
                    (i * 33).toLong(),
                    "Album ${i * 33}",
                    (i * 44).toLong(),
                    "Artist ${i * 44}",
                    i * 5,
                    "Genre ${i * 5}",
                    i,
                    "Playlist $i"
                )
            )
        }
        return tracks
    }

    override suspend fun getTracksByArtist(artistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksInAlbum(albumId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracksInPlaylist(playlistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumArt(albumId: Long): Bitmap {
        TODO("Not yet implemented")
    }
}
