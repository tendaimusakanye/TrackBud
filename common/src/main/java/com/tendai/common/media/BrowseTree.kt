package com.tendai.common.media

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.tendai.common.R
import com.tendai.common.ServiceLocator
import com.tendai.common.media.extensions.flag
import com.tendai.common.media.extensions.id
import com.tendai.common.media.extensions.title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Represents a tree of media that's used by [MusicService.onLoadChildren].
 *
 * [BrowseTree] maps a media id (see: [MediaMetadataCompat.METADATA_KEY_MEDIA_ID]) to one (or
 * more) [MediaMetadataCompat] objects, which are children of that media id.
 *
 * For example, given the following conceptual tree:
 *  +-- Discover
 *  |    +-- Album_A
 *  |    |    +-- Song_1
 *  |    |    +-- Song_2
 *  |    +-- Album_B
 *  |    +-- Playlists
 *  ...
 *  +-- Tracks
 *  +-- Artists
 *  .....
 *
 *  Requesting `browseTree["Discover"]` would return lists "Album_A","Album_B","Playlists" and
 *  any other direct children. All of these lists are browsable Taking the media ID of "Album_A" ("Album_A" in this example),
 *  `browseTree["Album_A"]` would return "Song_1" and "Song_2". Since those are leaf nodes,
 *  requesting `browseTree["Song_1"]` would return null (there aren't any children of it).
 *
 */
class BrowseTree(private val context: Context) {
    private val albumRepository by lazy {
        ServiceLocator.provideAlbumRepository(context)
    }
    private val artistRepository by lazy {
        ServiceLocator.provideArtistRepository(context)
    }
    private val playlistRepository by lazy {
        ServiceLocator.providePlaylistRepository(context)
    }
    private val tracksRepository by lazy {
        ServiceLocator.provideTracksRepository(context)
    }
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    init {
        addDiscoverRoot()


    }

    private fun addDiscoverRoot() {
        val discoverRoot = mediaIdToChildren[DISCOVER_ROOT] ?: mutableListOf()

        val albumsMetadata = MediaMetadataCompat.Builder().apply {
            this.id = TYPE_ALBUMS
            this.title = context.getString(R.string.albums_title)
            this.flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        val playlistsMetadata = MediaMetadataCompat.Builder().apply {
            this.id = TYPE_PLAYLISTS
            this.title = context.getString(R.string.tracks_title)
            this.flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }.build()

        discoverRoot += albumsMetadata
        discoverRoot += playlistsMetadata

        mediaIdToChildren[DISCOVER_ROOT] = discoverRoot
    }

}

const val DISCOVER_ROOT = "DISCOVER"
const val TRACKS_ROOT = "TRACKS"
const val ARTISTS_ROOT = "ARTISTS"
const val TYPE_TRACKS ="TRACKS"
const val TYPE_PLAYLISTS = "PLAYLISTS"
const val TYPE_ALBUMS = "ALBUMS"


//todo: Implement Dagger DI
//TODO: Should I implement onReadyListeners?
