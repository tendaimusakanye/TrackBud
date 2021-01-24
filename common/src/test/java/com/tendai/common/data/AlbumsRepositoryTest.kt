package com.tendai.common.data

import com.tendai.common.FakeAlbumDataSource
import com.tendai.common.media.source.AlbumRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AlbumRepositoryTest {
    private var albumsRepository: AlbumRepository? = null

    @Before
    fun setUp() {
        albumsRepository = AlbumRepository(FakeAlbumDataSource())
    }

    @Test
    fun getAlbums() {
        val allAlbums = albumsRepository?.getAlbums(2)
        Assert.assertEquals(2, allAlbums?.size)
    }

}