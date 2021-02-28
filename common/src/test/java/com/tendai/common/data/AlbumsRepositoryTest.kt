package com.tendai.common.data

import com.tendai.common.FakeAlbumDataSource
import com.tendai.common.source.AlbumRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AlbumRepositoryTest {
    private var albumsRepository: AlbumRepository? = null

    @Before
    fun setUp() {
        albumsRepository =
            AlbumRepository(FakeAlbumDataSource())
    }

    @After
    fun tearDown() {
        albumsRepository = null
    }

    @Test
    fun getAlbums() = runBlocking{
        val allAlbums = albumsRepository?.getAlbums(2)
        Assert.assertEquals(2, allAlbums?.size)
    }

}