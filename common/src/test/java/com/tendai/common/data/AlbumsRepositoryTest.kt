package com.tendai.common.data

import com.tendai.common.FakeAlbumDataSource
import com.tendai.common.source.AlbumRepository
import kotlinx.coroutines.runBlocking
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

    @Test
    fun getAlbums() = runBlocking{
        val allAlbums = albumsRepository?.getAlbums(2)
        Assert.assertEquals(100001, allAlbums?.size)
    }

}