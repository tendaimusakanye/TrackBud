package com.tendai.common.data.source.local

import android.database.MatrixCursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.test.ProviderTestCase2
import com.tendai.common.data.source.MyProvider

class AlbumDataSourceTest : ProviderTestCase2<MyProvider>(
    MyProvider::class.java, MediaStore.AUTHORITY
) {

    private var albumDataSource: AlbumDataSource? = null


    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        albumDataSource =
            AlbumDataSource(mockContext)
    }

    fun test_get_artists() {
        val limit = 2

        val projection = arrayOf(
            BaseColumns._ID,
            MediaStore.Audio.AlbumColumns.ALBUM_ID,
            MediaStore.Audio.AlbumColumns.ALBUM,
            MediaStore.Audio.AlbumColumns.ARTIST,
            MediaStore.Audio.AlbumColumns.ARTIST_ID,
            MediaStore.Audio.AlbumColumns.FIRST_YEAR,
            MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS
        )

        val albumOne =
            arrayOf(
                100, 2567, "Beer bottles and Bongs", "PostMalone", 1567, 2010, 10
            )
        val albumTwo =
            arrayOf(
                101, 2356, "Strings and Blings", "Nasty C", 1007, 2018, 12
            )
        val albumsThree =
            arrayOf(
                102, 2090, "Wonder", 1433, "Hillsong", 2019, 16
            )

        val albumsFour =
            arrayOf(
                102, 2453, "Send Me away", "Nasty C", 1007, 2020, 16
            )


        val matrixCursor = MatrixCursor(projection)
        matrixCursor.addRow(albumOne)
        matrixCursor.addRow(albumTwo)
        matrixCursor.addRow(albumsThree)
        matrixCursor.addRow(albumsFour)

        this.provider.addQueryResult(matrixCursor)

//        val albumsWithLimit = albumDataSource?.getAlbums(limit)
//        val albumWithGivenId = albumDataSource?.getAlbum(102)
//        val albumsForGivenArtist = albumDataSource?.getAlbumsForArtist(1007)

//        assertEquals(2, albumsWithLimit?.size)
//        Assert.assertEquals("Wonder", albumWithGivenId?.albumTitle)
//        assertEquals(2, albumsForGivenArtist?.size)

    }

}