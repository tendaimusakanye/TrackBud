package com.tendai.common.media.extensions

import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.model.Album
import com.tendai.common.media.source.model.Artist
import com.tendai.common.media.source.model.Playlist
import com.tendai.common.media.source.model.Track
import java.util.concurrent.TimeUnit

inline fun <reified T> MediaMetadataCompat.Builder.mapFrom(media: T): MediaMetadataCompat.Builder {
    when (T::class.simpleName) {
        Album::class.simpleName -> {
            val album = media as Album

            id = album.id.toString()
            this.album = album.albumTitle
            this.albumArtist = album.albumArtist
            year = album.yearReleased.toLong()
            trackCount = album.numberOfTracks.toLong()
            this.albumArtUri = album.albumArtUri.toString()
            flag = FLAG_BROWSABLE

            //for ease of displaying
            displayTitle = album.albumTitle
            displaySubtitle = album.albumArtist
            displayDescription = "${album.numberOfTracks} tracks"
            displayIconUri = album.albumArtUri.toString()

        }
        Album::class.simpleName -> {
            val artist = media as Artist

            id = artist.artistId.toString()
            this.artist = artist.artistName
            trackCount = artist.numberOfTracks.toLong()
            flag = FLAG_BROWSABLE
            displayTitle = artist.artistName
            displayDescription = "${artist.numberOfAlbums} albums|${artist.numberOfTracks} tracks"

        }
        Playlist::class.simpleName -> {
            val playlist = media as Playlist

            id = playlist.playlistId.toString()
            title = playlist.playlistName
            trackCount = playlist.numberOfTracks.toLong()
            flag = FLAG_BROWSABLE
            displayTitle = playlist.playlistName
            displayDescription = playlist.numberOfTracks.toString()
        }
        Track::class.simpleName -> {
            val track = media as Track
            val durationMs = TimeUnit.SECONDS.toMillis(track.duration.toLong())

            id = track.id.toString()
            this.title = track.trackName
            this.album = track.albumName
            this.artist = track.artistName
            duration = durationMs
            trackNumber = track.trackNumber.toString()
            albumArtUri = track.albumArtUri.toString()
            flag = FLAG_PLAYABLE
            displayTitle = track.trackName
            displaySubtitle = track.artistName
            displayDescription = track.albumName
            displayIconUri = track.albumArtUri.toString()
        }
        else -> throw ClassNotFoundException("${T::class.simpleName} does not exist")
    }
    return this
}

//todo: is there a better way to write this code ?
//todo: check what an Id looks like from results returned from mediaStore
//todo: then if need be create hierarchy aware mediaID or a MediaID Util class for convenience


