package com.tendai.common.media.extensions

import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.media.source.model.Album
import com.tendai.common.media.source.model.Artist
import com.tendai.common.media.source.model.Playlist
import com.tendai.common.media.source.model.Track

fun MediaMetadataCompat.Builder.mapFrom(
    album: Album? = null,
    artist: Artist? = null,
    playlist: Playlist? = null,
    track: Track? = null

): MediaMetadataCompat.Builder {
    if (album != null) {
        id = album.id.toString()
        this.album = album.albumTitle
        this.albumArtist = album.albumArtist
        year = album.yearReleased.toLong()
        trackCount = album.numberOfTracks.toLong()
        flag = FLAG_BROWSABLE

        displayTitle = album.albumTitle
        displaySubtitle = album.albumArtist
        displayDescription = "${album.numberOfTracks} tracks"

    }
    if (artist != null) {
        id = artist.artistId.toString()
        this.artist = artist.artistName
        trackCount = artist.numberOfTracks.toLong()
        flag = FLAG_BROWSABLE

        displayTitle = artist.artistName
        displayDescription = "${artist.numberOfAlbums} albums|${artist.numberOfTracks} tracks"

    }
    if (playlist != null) {
        id = playlist.playlistId.toString()
        title = playlist.playlistName
        trackCount = playlist.numberOfTracks.toLong()
        flag = FLAG_BROWSABLE

        displayTitle = playlist.playlistName
        displayDescription = playlist.numberOfTracks.toString()
    }
    if (track != null) {
        id = track.id.toString()
        this.title = track.trackName
        this.album = track.albumName
        this.artist = track.artistName
        duration = track.duration.toLong()
        trackNumber = track.trackNumber.toString()
        flag = FLAG_PLAYABLE

        displayTitle = track.trackName
        displaySubtitle = track.artistName
    }

    return this
}

//todo: check what an Id looks like from results returned from mediaStore
//todo: then if need be create hierarchy aware mediaID or a MediaID Util class for convenience


