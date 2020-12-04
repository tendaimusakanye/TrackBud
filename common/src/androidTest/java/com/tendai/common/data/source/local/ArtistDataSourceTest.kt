package com.tendai.common.data.source.local

import android.database.MatrixCursor
import android.provider.MediaStore
import android.test.ProviderTestCase2
import com.tendai.common.data.source.MyProvider

class ArtistDataSourceTest :
    ProviderTestCase2<MyProvider>(
        MyProvider::class.java, MediaStore.AUTHORITY
    ) {

    private var artistDataSource: ArtistDataSource? = null

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        artistDataSource =
            ArtistDataSource(mockContext)
    }

    // prefix the method with the word test for the test to run.
    fun test_getAllArtists_returns_all_artists() {
        val artistInserted =
            arrayOf(
                1567, "PostMalone", 10, 65
            )

        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS,
            MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS
        )

        val matrixCursor = MatrixCursor(projection)
        matrixCursor.addRow(artistInserted)
        this.provider.addQueryResult(matrixCursor)

        //remove suspend modifiers to test the content providers.
        val artistReturned = artistDataSource?.getAllArtists()
        assertEquals("PostMalone", artistReturned?.get(0)?.artistName)
    }

}